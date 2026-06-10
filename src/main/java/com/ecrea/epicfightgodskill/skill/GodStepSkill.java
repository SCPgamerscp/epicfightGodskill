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
 * GodStepSkill - 神移動 (DODGEスロット)
 *
 * Stepの強化版:
 *   前後左右4方向ステップ・距離5倍 (MOVEMENT_SPEED MULTIPLY_TOTAL +4.0)
 *   スタミナ消費なし (Resource.NONE)
 *   2秒(40tick)ごとに体力3回復 → PlayerTickEvent で実装
 */
public class GodStepSkill extends DodgeSkill {

    public static final UUID SPEED_BOOST_UUID =
            UUID.fromString("f1f0e9d8-c7b6-a5a4-9382-828170605040");
    private static final String SPEED_BOOST_NAME = "god_step_speed_boost";
    private static final UUID CAST_EVENT_UUID =
            UUID.fromString("a2b1c0d9-e8f7-b6c5-a4b3-c2d1e0f9a8b7");

    public static final int BOOST_TICKS = 14;

    public static DodgeSkill.Builder createGodStepBuilder() {
        // ローカル変数で DodgeSkill.Builder 型を保持し setAnimations を呼ぶ
        DodgeSkill.Builder b = DodgeSkill.createDodgeBuilder();
        b.setResource(Resource.NONE);
        b.setAnimations(
            Animations.BIPED_STEP_FORWARD,
            Animations.BIPED_STEP_BACKWARD,
            Animations.BIPED_STEP_LEFT,
            Animations.BIPED_STEP_RIGHT
        );
        return b;
    }

    public GodStepSkill(DodgeSkill.Builder builder) {
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
                        .startGodStepBoost(entity.getUUID());
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
