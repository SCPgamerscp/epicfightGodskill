package com.ecrea.epicfightgodskill.skill;

import com.ecrea.epicfightgodskill.EpicFightGodskillMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/** Automatically avoids direct combat hits while equipped in a passive slot. */
@Mod.EventBusSubscriber(modid = EpicFightGodskillMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AutoDodgeSkill extends PassiveSkill {
    private static final double STEP_DISTANCE = 1.5D;
    private static final double STEP_INCREMENT = 0.25D;

    @SuppressWarnings("unchecked")
    public static SkillBuilder<AutoDodgeSkill> createAutoDodgeBuilder() {
        return (SkillBuilder<AutoDodgeSkill>)(SkillBuilder<?>)
                PassiveSkill.createPassiveBuilder().setResource(Skill.Resource.NONE);
    }

    public AutoDodgeSkill(SkillBuilder<? extends PassiveSkill> builder) {
        super(builder);
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        DamageSource source = event.getSource();
        if (!isCombatAttack(source, player)) return;

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null || EpicFightGodskillMod.AUTODODGESKILL == null) return;

        // Let an actively held guard take precedence when both skills are equipped.
        SkillContainer guard = patch.getSkill(yesman.epicfight.skill.SkillSlots.GUARD);
        if (guard != null && guard.getSkill() == EpicFightGodskillMod.PERFECTGUARDSKILL
                && guard.isActivated()) return;

        boolean equipped = false;
        for (SkillSlot slot : EpicFightGodskillMod.ALL_PASSIVE_SLOTS) {
            SkillContainer container = patch.getSkill(slot);
            if (container != null && container.getSkill() == EpicFightGodskillMod.AUTODODGESKILL) {
                equipped = true;
                break;
            }
        }
        if (!equipped) return;

        // The incoming hit is avoided even if both sides are obstructed. Movement
        // only happens along a clear path, so walls cannot be crossed by the step.
        event.setCanceled(true);
        Vec3 origin = source.getSourcePosition();
        if (origin == null && source.getDirectEntity() != null) {
            origin = source.getDirectEntity().position();
        }
        if (origin == null) return;

        Vec3 incoming = player.position().subtract(origin);
        Vec3 side = new Vec3(-incoming.z, 0.0D, incoming.x);
        if (side.lengthSqr() < 1.0E-6D) {
            Vec3 facing = player.getLookAngle();
            side = new Vec3(-facing.z, 0.0D, facing.x);
        }
        side = side.normalize();
        Vec3 opposite = side.scale(-1.0D);
        boolean sideOpen = canStep(player, side);
        boolean oppositeOpen = canStep(player, opposite);
        Vec3 direction = sideOpen && oppositeOpen
                ? (player.getRandom().nextBoolean() ? side : opposite)
                : sideOpen ? side : oppositeOpen ? opposite : null;
        if (direction == null) return;

        Vec3 facing = player.getLookAngle();
        Vec3 playerRight = new Vec3(-facing.z, 0.0D, facing.x);
        patch.playAnimationSynchronized(direction.dot(playerRight) >= 0.0D
                ? Animations.BIPED_STEP_RIGHT : Animations.BIPED_STEP_LEFT, 0.0F);
        player.teleportTo(player.getX() + direction.x * STEP_DISTANCE,
                player.getY(), player.getZ() + direction.z * STEP_DISTANCE);
    }

    private static boolean isCombatAttack(DamageSource source, ServerPlayer player) {
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();
        if (attacker == player) return false;
        return (direct instanceof LivingEntity && attacker instanceof LivingEntity)
                || (direct instanceof Projectile && source.is(DamageTypeTags.IS_PROJECTILE));
    }

    private static boolean canStep(ServerPlayer player, Vec3 direction) {
        for (double distance = STEP_INCREMENT; distance <= STEP_DISTANCE; distance += STEP_INCREMENT) {
            AABB box = player.getBoundingBox().move(direction.x * distance, 0.0D,
                    direction.z * distance);
            if (!player.level().noCollision(player, box)) return false;
            // Do not automatically step off a ledge when starting on the ground.
            if (player.onGround() && player.level().noCollision(player, box.move(0.0D, -0.2D, 0.0D))) {
                return false;
            }
        }
        return true;
    }
}
