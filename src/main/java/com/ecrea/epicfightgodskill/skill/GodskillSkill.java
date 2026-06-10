package com.ecrea.epicfightgodskill.skill;

import java.util.UUID;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

/**
 * Godskill - 全能パッシブスキル
 *
 * attribute_modifiers (godskill.json) で付与:
 *   epicfight:staminar             +20   (最大スタミナ+20)
 *   epicfight:stamina_regen        +20   (スタミナ回復量+20)
 *   minecraft:generic.max_health   +20   (ハート+10)
 *   minecraft:generic.attack_damage  multiply_total +0.5 (攻撃ダメージ+50%)
 *   minecraft:generic.attack_speed   multiply_total +0.5 (攻撃速度+50%)
 *   epicfight:offhand_attack_speed   multiply_total +0.5 (オフハンド攻撃速度+50%)
 *
 * onInitiate() イベントで付与:
 *   スタミナ消費ゼロ              (SKILL_CONSUME_EVENT)
 *   武器固有スキル常時使用可能     (SKILL_CANCEL_EVENT でスタック自動回復)
 *   攻撃をドッジ/ガードでキャンセル可能 (ACTION_EVENT_SERVER)
 *   永続スタンシールド (Endurance式) - 被ダメージ時に自動回復
 *
 * Forge TickEvent で付与:
 *   2秒ごとに体力6回復
 *   スタンシールド自動維持
 */
public class GodskillSkill extends PassiveSkill {

    private static final UUID UUID_STAMINA_FREE  = UUID.fromString("a1b2c3d4-1111-2222-3333-aabbccddeeff");
    private static final UUID UUID_INNATE_RESET  = UUID.fromString("b2c3d4e5-1111-2222-3333-aabbccddeeff");
    private static final UUID UUID_ATTACK_CANCEL = UUID.fromString("c3d4e5f6-1111-2222-3333-aabbccddeeff");
    private static final UUID UUID_STUN_SHIELD   = UUID.fromString("d4e5f6a7-1111-2222-3333-aabbccddeeff");

    /** スタンシールドの値 (Endurance式) */
    private static final float STUN_SHIELD_VALUE = 1000.0F;

    @SuppressWarnings("unchecked")
    public static SkillBuilder<GodskillSkill> createGodskillBuilder() {
        return (SkillBuilder<GodskillSkill>)(SkillBuilder<?>)
                PassiveSkill.createPassiveBuilder();
    }

    public GodskillSkill(SkillBuilder<? extends PassiveSkill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);

        // ① スタミナを一切消費しない
        container.getExecutor().getEventListener().addEventListener(
            EventType.SKILL_CONSUME_EVENT, UUID_STAMINA_FREE, (event) -> {
                if (event.getResourceType() == Skill.Resource.STAMINA
                        && event.getSkill() != this) {
                    event.setResourceType(Skill.Resource.NONE);
                }
            }
        );

        // ② 武器固有スキル使用後スタックを即座にリセット → 常時使用可能
        container.getExecutor().getEventListener().addEventListener(
            EventType.SKILL_CANCEL_EVENT, UUID_INNATE_RESET, (event) -> {
                if (container.getExecutor().isLogicalClient()) return;
                SkillContainer innate = event.getSkillContainer();
                if (innate == null || innate.getSkill() == null) return;
                if (innate.getSkill().getCategory() != SkillCategories.WEAPON_INNATE) return;

                int max = Math.max(1, innate.getSkill().getMaxStack());
                // setStackSynchronize でサーバー→クライアントへ同期してリセット
                innate.getSkill().setStackSynchronize(innate, max);
            }
        );

        // ③ 攻撃モーション中にドッジ/ガードで割り込みを許可
        container.getExecutor().getEventListener().addEventListener(
            EventType.ACTION_EVENT_SERVER, UUID_ATTACK_CANCEL, (event) -> {
                if (event.getAnimation() == null) return;
                String name = event.getAnimation().toString().toLowerCase();
                if (name.contains("roll") || name.contains("step")
                        || name.contains("guard") || name.contains("parry")
                        || name.contains("dodge")) {
                    event.resetActionTick(true);
                }
            }
        );

        // ④ 永続スタンシールド (Endurance式) - 被ダメージ後にシールドを即回復
        container.getExecutor().getEventListener().addEventListener(
            EventType.TAKE_DAMAGE_EVENT_ATTACK, UUID_STUN_SHIELD, (event) -> {
                if (container.getExecutor().isLogicalClient()) return;
                // ダメージ処理後にスタンシールドを最大値に戻す
                container.getExecutor().setMaxStunShield(STUN_SHIELD_VALUE);
                container.getExecutor().setStunShield(STUN_SHIELD_VALUE);
            }
        );

        // 初期設定: スタンシールドを設定
        if (!container.getExecutor().isLogicalClient()) {
            container.getExecutor().setMaxStunShield(STUN_SHIELD_VALUE);
            container.getExecutor().setStunShield(STUN_SHIELD_VALUE);
        }
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecutor().getEventListener()
                .removeListener(EventType.SKILL_CONSUME_EVENT,  UUID_STAMINA_FREE);
        container.getExecutor().getEventListener()
                .removeListener(EventType.SKILL_CANCEL_EVENT,   UUID_INNATE_RESET);
        container.getExecutor().getEventListener()
                .removeListener(EventType.ACTION_EVENT_SERVER,  UUID_ATTACK_CANCEL);
        container.getExecutor().getEventListener()
                .removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, UUID_STUN_SHIELD);

        // スタンシールドをリセット
        if (!container.getExecutor().isLogicalClient()) {
            container.getExecutor().setStunShield(0.0F);
            container.getExecutor().setMaxStunShield(0.0F);
        }
    }

    /** 装備中は常にGUIに表示 */
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }
}
