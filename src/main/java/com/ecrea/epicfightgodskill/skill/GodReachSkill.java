package com.ecrea.epicfightgodskill.skill;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;

/**
 * GodReachSkill - ゴッドリーチ（神のリーチ）
 *
 * attribute_modifiers (god_reach.json):
 *   minecraft:generic.attack_damage multiply_total +0.5 (攻撃力+50%)
 *
 * リーチ5倍:
 *   MixinLivingEntityPatchReach で LivingEntityPatch.getReach() をインターセプト
 *
 * 体力回復:
 *   2秒(40tick)ごとに体力3回復 → PlayerTickEvent で実装
 */
public class GodReachSkill extends PassiveSkill {

    @SuppressWarnings("unchecked")
    public static SkillBuilder<GodReachSkill> createGodReachBuilder() {
        return (SkillBuilder<GodReachSkill>)(SkillBuilder<?>)
                PassiveSkill.createPassiveBuilder()
                .setResource(Skill.Resource.NONE);
    }

    public GodReachSkill(SkillBuilder<? extends PassiveSkill> builder) {
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
