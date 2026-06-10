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

    private static final SkillSlot[] MIXIN_PASSIVE_SLOTS = new SkillSlot[]{
        SkillSlots.PASSIVE1,     SkillSlots.PASSIVE2,     SkillSlots.PASSIVE3,
        ModSkillSlots.PASSIVE4,  ModSkillSlots.PASSIVE5,  ModSkillSlots.PASSIVE6,
        ModSkillSlots.PASSIVE7,  ModSkillSlots.PASSIVE8,  ModSkillSlots.PASSIVE9,
        ModSkillSlots.PASSIVE10, ModSkillSlots.PASSIVE11, ModSkillSlots.PASSIVE12,
        ModSkillSlots.PASSIVE13, ModSkillSlots.PASSIVE14, ModSkillSlots.PASSIVE15,
        ModSkillSlots.PASSIVE16, ModSkillSlots.PASSIVE17, ModSkillSlots.PASSIVE18,
        ModSkillSlots.PASSIVE19, ModSkillSlots.PASSIVE20, ModSkillSlots.PASSIVE21,
        ModSkillSlots.PASSIVE22, ModSkillSlots.PASSIVE23, ModSkillSlots.PASSIVE24,
        ModSkillSlots.PASSIVE25, ModSkillSlots.PASSIVE26, ModSkillSlots.PASSIVE27,
        ModSkillSlots.PASSIVE28, ModSkillSlots.PASSIVE29, ModSkillSlots.PASSIVE30,
        ModSkillSlots.PASSIVE31, ModSkillSlots.PASSIVE32, ModSkillSlots.PASSIVE33,
        ModSkillSlots.PASSIVE34, ModSkillSlots.PASSIVE35, ModSkillSlots.PASSIVE36,
        ModSkillSlots.PASSIVE37, ModSkillSlots.PASSIVE38, ModSkillSlots.PASSIVE39,
        ModSkillSlots.PASSIVE40, ModSkillSlots.PASSIVE41, ModSkillSlots.PASSIVE42,
        ModSkillSlots.PASSIVE43, ModSkillSlots.PASSIVE44, ModSkillSlots.PASSIVE45,
        ModSkillSlots.PASSIVE46, ModSkillSlots.PASSIVE47, ModSkillSlots.PASSIVE48,
        ModSkillSlots.PASSIVE49, ModSkillSlots.PASSIVE50
    };

    /** 突進距離(getReach)を5倍にする */
    @Inject(method = "getReach", at = @At("RETURN"), cancellable = true, remap = false)
    private void onGetReach(InteractionHand hand, CallbackInfoReturnable<Float> cir) {
        if (!(((Object) this) instanceof PlayerPatch<?> playerPatch)) return;
        if (EpicFightGodskillMod.GODREACHSKILL == null) return;
        for (SkillSlot slot : MIXIN_PASSIVE_SLOTS) {
            SkillContainer c = playerPatch.getSkill(slot);
            if (c != null && c.getSkill() == EpicFightGodskillMod.GODREACHSKILL) {
                cir.setReturnValue(Math.max(cir.getReturnValue(), 1.0f) * 5.0f);
                return;
            }
        }
    }
}
