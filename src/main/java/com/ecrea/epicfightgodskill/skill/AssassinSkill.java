package com.ecrea.epicfightgodskill.skill;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;

/**
 * AssassinSkill - 暗殺者
 *
 * attribute_modifiers (assassin.json):
 *   minecraft:generic.attack_damage multiply_total +0.5 (攻撃力+50%)
 *
 * ダガーのダメージ上限撤廃:
 *   MixinExtraDamageInstance で Eviscerate(ダガー固有スキル)の
 *   「対象の欠損体力ボーナスダメージ」の上限(damage_cap=20.0)を
 *   このスキル装備時のみ無制限化する。
 *
 * 体力回復:
 *   2秒(40tick)ごとに体力3回復 → PlayerTickEvent で実装
 */
public class AssassinSkill extends PassiveSkill {

    @SuppressWarnings("unchecked")
    public static SkillBuilder<AssassinSkill> createAssassinBuilder() {
        return (SkillBuilder<AssassinSkill>)(SkillBuilder<?>)
                PassiveSkill.createPassiveBuilder()
                .setResource(Skill.Resource.NONE);
    }

    public AssassinSkill(SkillBuilder<? extends PassiveSkill> builder) {
        super(builder);
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }
}
