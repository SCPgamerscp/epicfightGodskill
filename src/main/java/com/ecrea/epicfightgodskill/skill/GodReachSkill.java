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
 * 15ブロック範囲攻撃:
 *   MixinColliderReach で Collider/MultiCollider.updateAndSelectCollideEntity をフック
 *   攻撃判定を15ブロック範囲に拡大（MOD武器・攻撃速度変更にも対応）
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
    public boolean shouldDraw(SkillContainer container) {
        return true;
    }
}
