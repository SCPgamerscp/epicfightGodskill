package com.ecrea.epicfightgodskill.mixin;

import com.ecrea.epicfightgodskill.EpicFightGodskillMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

/**
 * ExtraDamageInstance.get() をインターセプトし、
 * Eviscerate(ダガー固有スキル)の「対象の欠損体力ボーナスダメージ」について
 * 暗殺者スキル装備中のプレイヤーのみ damage_cap(20.0) を撤廃して無制限化する。
 *
 * 元の計算式:
 *   min((target.maxHealth - target.health) * (params[0] + 0.05F * tier), damageCap)
 * 暗殺者装備時:
 *   (target.maxHealth - target.health) * (params[0] + 0.05F * tier)  ← cap なし
 */
@Mixin(value = ExtraDamageInstance.class, remap = false)
public abstract class MixinExtraDamageInstance {

    @Shadow(remap = false)
    private ExtraDamageInstance.ExtraDamage calculator;

    @Inject(method = "get", at = @At("RETURN"), cancellable = true, remap = false)
    private void epicfightgodskill$onGet(LivingEntity attacker, ItemStack hurtItem,
                                          LivingEntity target, float baseDamage,
                                          CallbackInfoReturnable<Float> cir) {
        // Eviscerate(対象の欠損体力ボーナス)のみ対象
        if (this.calculator != ExtraDamageInstance.EVISCERATE_LOST_HEALTH) return;
        if (target == null) return;
        if (EpicFightGodskillMod.ASSASSINSKILL == null) return;

        if (!(attacker instanceof Player player)) return;
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        boolean hasAssassin = false;
        for (SkillSlot slot : EpicFightGodskillMod.ALL_PASSIVE_SLOTS) {
            SkillContainer c = patch.getSkill(slot);
            if (c != null && c.getSkill() == EpicFightGodskillMod.ASSASSINSKILL) {
                hasAssassin = true;
                break;
            }
        }
        if (!hasAssassin) return;

        // damage_cap なしで再計算
        int tier = 0;
        if (hurtItem.getItem() instanceof TieredItem tieredItem) {
            tier += tieredItem.getTier().getLevel();
        }

        float[] params = ((ExtraDamageInstance) (Object) this).getParams();
        float uncapped = (target.getMaxHealth() - target.getHealth()) * (params[0] + 0.05F * tier);
        cir.setReturnValue(uncapped);
    }
}
