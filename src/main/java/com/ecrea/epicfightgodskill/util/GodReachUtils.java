package com.ecrea.epicfightgodskill.util;

import com.ecrea.epicfightgodskill.EpicFightGodskillMod;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class GodReachUtils {
    public static boolean hasGodReach(LivingEntityPatch<?> entitypatch) {
        if (!(entitypatch instanceof PlayerPatch<?> playerPatch)) return false;
        if (EpicFightGodskillMod.GODREACHSKILL == null) return false;
        for (SkillSlot slot : EpicFightGodskillMod.ALL_PASSIVE_SLOTS) {
            SkillContainer c = playerPatch.getSkill(slot);
            if (c != null && c.getSkill() == EpicFightGodskillMod.GODREACHSKILL) {
                return true;
            }
        }
        return false;
    }
}
