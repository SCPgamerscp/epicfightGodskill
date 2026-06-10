package com.ecrea.epicfightgodskill.skill;

import java.util.UUID;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

/**
 * GodDodgeSkill - 神回避 (DODGEスロット)
 *
 * ROLLの強化版:
 *   距離5倍 (MOVEMENT_SPEED MULTIPLY_TOTAL +4.0 → moveMultiplier=5.0)
 *   スタミナ消費なし (Resource.NONE)
 *   2秒(40tick)ごとに体力3回復 → PlayerTickEvent で実装
 */
public class GodDodgeSkill extends DodgeSkill {

    public static final UUID SPEED_BOOST_UUID =
            UUID.fromString("d0d9e8f7-c6b5-a4a3-8291-807060504030");
    private static final String SPEED_BOOST_NAME = "god_dodge_speed_boost";
    private static final UUID CAST_EVENT_UUID =
            UUID.fromString("e1e0f9a8-d7c6-b5b4-9382-918171615141");

    public static final int BOOST_TICKS = 14;

    public static DodgeSkill.Builder createGodDodgeBuilder() {
        // ローカル変数で DodgeSkill.Builder 型を保持し setAnimations を呼ぶ
        DodgeSkill.Builder b = DodgeSkill.createDodgeBuilder();
        b.setResource(Resource.NONE);
        b.setAnimations(
            Animations.BIPED_ROLL_FORWARD,
            Animations.BIPED_ROLL_BACKWARD
        );
        return b;
    }

    public GodDodgeSkill(DodgeSkill.Builder builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);

        container.getExecutor().getEventListener().addEventListener(
            EventType.SKILL_CAST_EVENT, CAST_EVENT_UUID, (event) -> {
                if (container.getExecutor().isLogicalClient()) return;
                if (event.getSkillContainer() != container) return;

                LivingEntity entity = container.getExecutor().getOriginal();
                var attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attr == null) return;

                if (attr.getModifier(SPEED_BOOST_UUID) == null) {
                    attr.addTransientModifier(new AttributeModifier(
                        SPEED_BOOST_UUID,
                        SPEED_BOOST_NAME,
                        4.0,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }

                com.ecrea.epicfightgodskill.EpicFightGodskillMod
                        .startGodDodgeBoost(entity.getUUID());
            }
        );
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecutor().getEventListener()
                .removeListener(EventType.SKILL_CAST_EVENT, CAST_EVENT_UUID);

        if (!container.getExecutor().isLogicalClient()) {
            removeBoost(container.getExecutor().getOriginal());
        }
    }

    public static void removeBoost(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(SPEED_BOOST_UUID) != null) {
            attr.removeModifier(SPEED_BOOST_UUID);
        }
    }
}
