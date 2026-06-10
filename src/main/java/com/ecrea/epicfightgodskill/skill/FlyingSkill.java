package com.ecrea.epicfightgodskill.skill;


import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;

/**
 * FlyingSkill - 飛行 (MOVERスロット)
 *
 * 効果:
 *   装備中クリエイティブ飛行が有効になる (mayfly + flying = true)
 *   2秒(40tick)ごとに体力3回復
 *   → 回復は EpicFightGodskillMod の PlayerTickEvent で実装
 *   → 飛行維持も PlayerTickEvent で毎tick setFlying(true) してリセット防止
 *
 * onInitiate : サーバー側で即座に飛行を有効化
 * onRemoved  : サーバー側で飛行を無効化
 */
public class FlyingSkill extends Skill {

    public static SkillBuilder<FlyingSkill> createFlyingBuilder() {
        return new SkillBuilder<FlyingSkill>()
                .setCategory(SkillCategories.MOVER)
                .setActivateType(Skill.ActivateType.TOGGLE)
                .setResource(Resource.NONE);
    }

    public FlyingSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    /** 装備時: サーバー側で飛行を即時有効化 (TickEvent側で管理するため何もしない) */
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
    }

    /** 取り外し時: サーバー側で飛行を無効化 (TickEvent側で管理するため何もしない) */
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
    }

    /** GUI に常に表示 */
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }

    /** 飛行を有効化 (未使用) */
    public static void enableFlight(SkillContainer container) {
    }

    /** 飛行を無効化 (未使用) */
    public static void disableFlight(SkillContainer container) {
    }
}
