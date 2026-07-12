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

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }
}
