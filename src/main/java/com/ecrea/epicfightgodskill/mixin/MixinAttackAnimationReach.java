package com.ecrea.epicfightgodskill.mixin;

import com.ecrea.epicfightgodskill.EpicFightGodskillMod;
import com.ecrea.epicfightgodskill.ModSkillSlots;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.List;

/**
 * AttackAnimation.hurtCollidingEntities をインターセプトし、
 * ゴッドリーチスキル装備時に攻撃範囲を15ブロックに拡大する。
 *
 * 通常の Epic Fight では攻撃の当たり判定はアニメーション内蔵の Collider (OBB) で決まるが、
 * このMixinでは攻撃フレーム中にプレイヤー周囲15ブロックの全エンティティに
 * ダメージを与えることでリーチ5倍を実現する。
 */
@Mixin(value = AttackAnimation.class, remap = false)
public class MixinAttackAnimationReach {

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
     * hurtCollidingEntities の後に追加処理を挿入。
     * ゴッドリーチ装備時は、通常のコリジョン判定で拾えなかった周囲15ブロック以内の
     * エンティティに対しても攻撃を試みる。
     */
    @Inject(method = "hurtCollidingEntities", at = @At("TAIL"), remap = false)
    protected void afterHurtCollidingEntities(
            LivingEntityPatch<?> entitypatch,
            float prevElapsedTime,
            float elapsedTime,
            EntityState prevState,
            EntityState state,
            AttackAnimation.Phase phase,
            CallbackInfo ci
    ) {
        if (!hasGodReach(entitypatch)) return;
        if (entitypatch.isLogicalClient()) return;
        // 攻撃フレーム中でなければ何もしない
        if (!prevState.attacking() && !state.attacking()) return;

        AttackAnimation self = (AttackAnimation)(Object) this;
        LivingEntity entity = entitypatch.getOriginal();
        double reachRange = 15.0;

        // 15ブロック範囲のAABBでエンティティを取得
        AABB searchBox = entity.getBoundingBox().inflate(reachRange, reachRange, reachRange);
        List<Entity> nearbyEntities = entity.level().getEntities(entity, searchBox, e -> {
            if (e.isSpectator()) return false;
            if (!(e instanceof LivingEntity)) return false;
            if (e == entity) return false;
            return true;
        });

        for (Entity target : nearbyEntities) {
            LivingEntity trueEntity = null;
            if (target instanceof LivingEntity le) {
                trueEntity = le;
            }

            if (trueEntity == null || !trueEntity.isAlive()) continue;

            // 既に通常コリジョンでヒットしたエンティティはスキップ
            if (entitypatch.getCurrentlyAttackTriedEntities().contains(trueEntity)) continue;
            if (entitypatch.isTargetInvulnerable(target)) continue;

            try {
                EpicFightDamageSource damagesource = self.getEpicFightDamageSource(entitypatch, target, phase);
                int prevInvulTime = target.invulnerableTime;
                target.invulnerableTime = 0;

                AttackResult attackResult = entitypatch.attack(damagesource, target, phase.hand);
                target.invulnerableTime = prevInvulTime;

                if (attackResult.resultType.dealtDamage()) {
                    entitypatch.setLastAttackSuccess(true);
                }

                entitypatch.getCurrentlyAttackTriedEntities().add(trueEntity);

                if (attackResult.resultType.shouldCount()) {
                    entitypatch.getCurrentlyActuallyHitEntities().add(trueEntity);
                }
            } catch (Exception e) {
                // 安全対策: 何らかのエラーが発生しても他のエンティティの処理は続行
            }
        }
    }
}
