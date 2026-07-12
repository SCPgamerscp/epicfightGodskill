package com.ecrea.epicfightgodskill.skill;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;

/**
 * AngelSkill - エンジェルスキル
 *
 * attribute_modifiers (angelskill.json):
 *   minecraft:generic.attack_damage multiply_total +0.5 (攻撃ダメージ+50%)
 *
 * tick-based回復:
 *   2秒(40tick)ごとに体力3回復
 *   → EpicFightGodskillMod の PlayerTickEvent で実装
 */
public class AngelSkill extends PassiveSkill {

    @SuppressWarnings("unchecked")
    public static SkillBuilder<AngelSkill> createAngelBuilder() {
        return (SkillBuilder<AngelSkill>)(SkillBuilder<?>)
                PassiveSkill.createPassiveBuilder()
                .setResource(Skill.Resource.NONE);
    }

    public AngelSkill(SkillBuilder<? extends PassiveSkill> builder) {
        super(builder);
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }
}
