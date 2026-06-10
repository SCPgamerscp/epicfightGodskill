package com.ecrea.epicfightgodskill.mixin;

import com.ecrea.epicfightgodskill.EpicFightGodskillMod;
import com.ecrea.epicfightgodskill.ModSkillSlots;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/**
 * LivingEntityPatch.getReach() をインターセプトし、
 * プレイヤーがゴッドリーチスキルを装備中のとき突進リーチを5倍にする。
 */
@Mixin(value = LivingEntityPatch.class, remap = false)
public class MixinLivingEntityPatchReach {


    /** 突進距離(getReach)を5倍にする */
    @Inject(method = "getReach", at = @At("RETURN"), cancellable = true, remap = false)
    private void onGetReach(InteractionHand hand, CallbackInfoReturnable<Float> cir) {
        if (!(((Object) this) instanceof PlayerPatch<?> playerPatch)) return;
        if (EpicFightGodskillMod.GODREACHSKILL == null) return;
        for (SkillSlot slot : EpicFightGodskillMod.ALL_PASSIVE_SLOTS) {
            SkillContainer c = playerPatch.getSkill(slot);
            if (c != null && c.getSkill() == EpicFightGodskillMod.GODREACHSKILL) {
                cir.setReturnValue(Math.max(cir.getReturnValue(), 1.0f) * 5.0f);
                return;
            }
        }
    }
}
