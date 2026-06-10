package com.ecrea.epicfightgodskill;

import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlot;

public enum ModSkillSlots implements SkillSlot {
    PASSIVE4(SkillCategories.PASSIVE),
    PASSIVE5(SkillCategories.PASSIVE),
    PASSIVE6(SkillCategories.PASSIVE),
    PASSIVE7(SkillCategories.PASSIVE),
    PASSIVE8(SkillCategories.PASSIVE),
    PASSIVE9(SkillCategories.PASSIVE),
    PASSIVE10(SkillCategories.PASSIVE),
    PASSIVE11(SkillCategories.PASSIVE),
    PASSIVE12(SkillCategories.PASSIVE),
    PASSIVE13(SkillCategories.PASSIVE),
    PASSIVE14(SkillCategories.PASSIVE),
    PASSIVE15(SkillCategories.PASSIVE),
    PASSIVE16(SkillCategories.PASSIVE),
    PASSIVE17(SkillCategories.PASSIVE),
    PASSIVE18(SkillCategories.PASSIVE),
    PASSIVE19(SkillCategories.PASSIVE),
    PASSIVE20(SkillCategories.PASSIVE),
    PASSIVE21(SkillCategories.PASSIVE),
    PASSIVE22(SkillCategories.PASSIVE),
    PASSIVE23(SkillCategories.PASSIVE),
    PASSIVE24(SkillCategories.PASSIVE),
    PASSIVE25(SkillCategories.PASSIVE),
    PASSIVE26(SkillCategories.PASSIVE),
    PASSIVE27(SkillCategories.PASSIVE),
    PASSIVE28(SkillCategories.PASSIVE),
    PASSIVE29(SkillCategories.PASSIVE),
    PASSIVE30(SkillCategories.PASSIVE),
    PASSIVE31(SkillCategories.PASSIVE),
    PASSIVE32(SkillCategories.PASSIVE),
    PASSIVE33(SkillCategories.PASSIVE),
    PASSIVE34(SkillCategories.PASSIVE),
    PASSIVE35(SkillCategories.PASSIVE),
    PASSIVE36(SkillCategories.PASSIVE),
    PASSIVE37(SkillCategories.PASSIVE),
    PASSIVE38(SkillCategories.PASSIVE),
    PASSIVE39(SkillCategories.PASSIVE),
    PASSIVE40(SkillCategories.PASSIVE),
    PASSIVE41(SkillCategories.PASSIVE),
    PASSIVE42(SkillCategories.PASSIVE),
    PASSIVE43(SkillCategories.PASSIVE),
    PASSIVE44(SkillCategories.PASSIVE),
    PASSIVE45(SkillCategories.PASSIVE),
    PASSIVE46(SkillCategories.PASSIVE),
    PASSIVE47(SkillCategories.PASSIVE),
    PASSIVE48(SkillCategories.PASSIVE),
    PASSIVE49(SkillCategories.PASSIVE),
    PASSIVE50(SkillCategories.PASSIVE);

    private final yesman.epicfight.skill.SkillCategory category;
    private final int id;

    ModSkillSlots(yesman.epicfight.skill.SkillCategory category) {
        this.category = category;
        this.id = SkillSlot.ENUM_MANAGER.assign(this);
    }

    @Override
    public yesman.epicfight.skill.SkillCategory category() {
        return this.category;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
