package com.ecrea.epicfightgodskill.skill;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent;
import yesman.epicfight.gameasset.EpicFightSounds;

/**
 * PerfectGuardSkill - パーフェクトガード (GUARDスロット)
 *
 * GuardSkillの上位互換:
 *   360度全方向ガード (isFront チェックを除去)
 *   スタミナ消費なし (getPenalizer()=0, Resource.NONE)
 *   2秒(40tick)ごとに体力3回復 → PlayerTickEvent で実装
 */
public class PerfectGuardSkill extends GuardSkill {

    /** 360度ガード用の独自イベントUUID */
    private static final UUID PERFECT_EVENT_UUID =
            UUID.fromString("c2c1d0ef-e8d7-c6c5-a4b3-b2a19080706f");

    public static GuardSkill.Builder createPerfectGuardBuilder() {
        // GuardSkill の全武器モーションをそのまま流用
        return GuardSkill.createGuardBuilder()
                .setResource(Resource.NONE);   // スタミナ消費なし表示
    }

    public PerfectGuardSkill(GuardSkill.Builder builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        // super.onInitiate() で MOVEMENT_INPUT / DEAL_DAMAGE / 元の TAKE_DAMAGE リスナーを登録
        super.onInitiate(container);

        // 元の前面チェック付き TAKE_DAMAGE_EVENT_ATTACK リスナーを削除
        container.getExecutor().getEventListener()
                .removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, 1);

        // 360度ガード版を再登録 (同じ priority=1)
        container.getExecutor().getEventListener().addEventListener(
            EventType.TAKE_DAMAGE_EVENT_ATTACK, PERFECT_EVENT_UUID, (event) -> {
                if (!container.isActivated()) return;
                if (event.getPlayerPatch().getHoldingSkill() != this) return;

                DamageSource damageSource = event.getDamageSource();
                float impact   = 0.5F;
                float knockback = 0.25F;

                if (damageSource instanceof EpicFightDamageSource epicDS) {
                    // GUARD_PUNCTURE チェックなし → 貫通攻撃もガードする
                    impact   = epicDS.calculateImpact();
                    knockback += Math.min(impact * 0.1F, 1.0F);
                }

                CapabilityItem itemCap = event.getPlayerPatch()
                        .getHoldingItemCapability(InteractionHand.MAIN_HAND);

                // 全方向ガード (isFront チェックなし)
                this.guard(container, itemCap, event, knockback, impact, false);
            }, 1
        );
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecutor().getEventListener()
                .removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, PERFECT_EVENT_UUID, 1);
    }

    /** どんなダメージソースでもガード可能にする (矢やガード不可攻撃も含む) */
    @Override
    protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
        return true;
    }

    /**
     * スタミナ消費ゼロ版 guard()。
     * penalizer=0 なので consumeAmount=0 → canAfford=true (ガードブレークなし)
     */
    @Override
    public void guard(SkillContainer container, CapabilityItem itemCapability,
                      TakeDamageEvent.Attack event, float knockback, float impact, boolean advanced) {

        DamageSource damageSource = event.getDamageSource();
        Entity offender = getOffender(damageSource);

        event.getPlayerPatch().playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
        ServerPlayer sp = event.getPlayerPatch().getOriginal();
        EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(
                sp.serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, sp, offender);

        if (offender instanceof LivingEntity le) {
            knockback += EnchantmentHelper.getKnockbackBonus(le) * 0.1F;
        }

        // consumeAmount = 0 → スタミナ消費なし & ガードブレークなし
        event.getPlayerPatch().consumeForSkill(this, Skill.Resource.STAMINA, 0.0F);

        event.getPlayerPatch().knockBackEntity(offender.position(), knockback);
        container.getDataManager().setDataSync(SkillDataKeys.PENALTY.get(), 0.0F);
        container.getDataManager().setDataSync(SkillDataKeys.PENALTY_RESTORE_COUNTER.get(),
                container.getServerExecutor().getOriginal().tickCount);

        // 常に GUARD アニメーション (ガードブレークなし)
        var animation = this.getGuardMotion(container, event.getPlayerPatch(),
                                             itemCapability, BlockType.GUARD);
        if (animation != null) {
            event.getPlayerPatch().playAnimationSynchronized(animation, 0.0F);
        }

        this.dealEvent(event.getPlayerPatch(), event, advanced);
    }

    /** ペナルティは常に0 → consumeAmount = 0 → スタミナ消費なし */
    @Override
    protected float getPenalizer(CapabilityItem itemCapability) {
        return 0.0F;
    }

    /** ペナルティが常に0なのでGUIに表示不要 */
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return false;
    }
}
