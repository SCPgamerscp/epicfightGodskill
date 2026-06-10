package com.ecrea.epicfightgodskill.mixin;

import com.ecrea.epicfightgodskill.EpicFightGodskillMod;
import com.ecrea.epicfightgodskill.ModSkillSlots;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiCollider;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

/**
 * Collider と MultiCollider の両方の updateAndSelectCollideEntity をインターセプトし、
 * ゴッドリーチスキル装備時に攻撃範囲を15ブロックに拡大する。
 *
 * MultiCollider は Collider の updateAndSelectCollideEntity を完全にオーバーライドしているため、
 * 両方にMixinを適用する必要がある。
 */
@Mixin(value = {Collider.class, MultiCollider.class}, remap = false)
public class MixinColliderReach {

    private static final SkillSlot[] PASSIVE_SLOTS = new SkillSlot[]{
        SkillSlots.PASSIVE1,     SkillSlots.PASSIVE2,     SkillSlots.PASSIVE3,
        ModSkillSlots.PASSIVE4,  ModSkillSlots.PASSIVE5,  ModSkillSlots.PASSIVE6,
        ModSkillSlots.PASSIVE7,  ModSkillSlots.PASSIVE8,  ModSkillSlots.PASSIVE9,
        ModSkillSlots.PASSIVE10, ModSkillSlots.PASSIVE11, ModSkillSlots.PASSIVE12,
        ModSkillSlots.PASSIVE13, ModSkillSlots.PASSIVE14, ModSkillSlots.PASSIVE15,
        ModSkillSlots.PASSIVE16, ModSkillSlots.PASSIVE17, ModSkillSlots.PASSIVE18,
        ModSkillSlots.PASSIVE19, ModSkillSlots.PASSIVE20, ModSkillSlots.PASSIVE21,
        ModSkillSlots.PASSIVE22, ModSkillSlots.PASSIVE23, ModSkillSlots.PASSIVE24,
        ModSkillSlots.PASSIVE25, ModSkillSlots.PASSIVE26, ModSkillSlots.PASSIVE27,
        ModSkillSlots.PASSIVE28, ModSkillSlots.PASSIVE29, ModSkillSlots.PASSIVE30,
        ModSkillSlots.PASSIVE31, ModSkillSlots.PASSIVE32, ModSkillSlots.PASSIVE33,
        ModSkillSlots.PASSIVE34, ModSkillSlots.PASSIVE35, ModSkillSlots.PASSIVE36,
        ModSkillSlots.PASSIVE37, ModSkillSlots.PASSIVE38, ModSkillSlots.PASSIVE39,
        ModSkillSlots.PASSIVE40, ModSkillSlots.PASSIVE41, ModSkillSlots.PASSIVE42,
        ModSkillSlots.PASSIVE43, ModSkillSlots.PASSIVE44, ModSkillSlots.PASSIVE45,
        ModSkillSlots.PASSIVE46, ModSkillSlots.PASSIVE47, ModSkillSlots.PASSIVE48,
        ModSkillSlots.PASSIVE49, ModSkillSlots.PASSIVE50
    };

    private static boolean hasGodReach(LivingEntityPatch<?> entitypatch) {
        if (!(entitypatch instanceof PlayerPatch<?> playerPatch)) return false;
        if (EpicFightGodskillMod.GODREACHSKILL == null) return false;
        for (SkillSlot slot : PASSIVE_SLOTS) {
            SkillContainer c = playerPatch.getSkill(slot);
            if (c != null && c.getSkill() == EpicFightGodskillMod.GODREACHSKILL) {
                return true;
            }
        }
        return false;
    }

    /**
     * updateAndSelectCollideEntity の RETURN 後に、ゴッドリーチ装備時は
     * 通常のコリジョン結果に加え、15ブロック範囲のエンティティを追加する。
     *
     * Collider と MultiCollider の両方に適用される。
     */
    @Inject(method = "updateAndSelectCollideEntity", at = @At("RETURN"), remap = false)
    private void afterUpdateAndSelectCollideEntity(
            LivingEntityPatch<?> entitypatch,
            AttackAnimation attackAnimation,
            float prevElapsedTime,
            float elapsedTime,
            Joint joint,
            float attackSpeed,
            CallbackInfoReturnable<List<Entity>> cir
    ) {
        if (!hasGodReach(entitypatch)) return;

        LivingEntity attacker = entitypatch.getOriginal();
        List<Entity> originalList = cir.getReturnValue();
        double reachRange = 15.0;

        // 15ブロック範囲のAABBでエンティティを取得
        AABB searchBox = attacker.getBoundingBox().inflate(reachRange, reachRange, reachRange);
        List<Entity> nearbyEntities = attacker.level().getEntities(attacker, searchBox, e -> {
            if (e.isSpectator()) return false;
            if (!(e instanceof LivingEntity)) return false;
            if (e == attacker) return false;
            return true;
        });

        // 元のリストに含まれていないエンティティを追加
        for (Entity e : nearbyEntities) {
            if (!originalList.contains(e)) {
                originalList.add(e);
            }
        }
    }
}
