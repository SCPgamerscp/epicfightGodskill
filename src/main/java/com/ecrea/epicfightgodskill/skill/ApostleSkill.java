package com.ecrea.epicfightgodskill.skill;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;

/**
 * ApostleSkill - 使徒スキル
 *
 * attribute_modifiers (apostleskill.json):
 *   minecraft:generic.attack_damage multiply_total +0.5 (攻撃ダメージ+50%)
 *
 * tick-based回復:
 *   2秒(40tick)ごとに体力2回復
 *   → EpicFightGodskillMod の PlayerTickEvent で実装
 */
public class ApostleSkill extends PassiveSkill {

    @SuppressWarnings("unchecked")
    public static SkillBuilder<ApostleSkill> createApostleBuilder() {
        return (SkillBuilder<ApostleSkill>)(SkillBuilder<?>)
                PassiveSkill.createPassiveBuilder()
                .setResource(Skill.Resource.NONE);
    }

    public ApostleSkill(SkillBuilder<? extends PassiveSkill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }
}
