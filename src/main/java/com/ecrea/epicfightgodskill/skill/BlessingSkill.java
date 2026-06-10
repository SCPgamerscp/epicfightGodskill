package com.ecrea.epicfightgodskill.skill;

import yesman.epicfight.skill.identity.RevelationSkill;

/**
 * BlessingSkill - 祝福 (Revelation/IDENTITYスロット)
 *
 * 効果:
 *   2秒(40tick)ごとに体力5回復
 *   → EpicFightGodskillMod の PlayerTickEvent で実装
 *
 * RevelationSkill を継承することで IDENTITY スロットに装備可能。
 * スタック/アニメーション等の Revelation 固有動作はそのまま保持。
 */
public class BlessingSkill extends RevelationSkill {

    public static RevelationSkill.Builder createBlessingBuilder() {
        return createRevelationSkillBuilder();
    }

    public BlessingSkill(Builder builder) {
        super(builder);
    }
}
