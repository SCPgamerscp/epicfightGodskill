package com.ecrea.epicfightgodskill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.ecrea.epicfightgodskill.skill.AngelSkill;
import com.ecrea.epicfightgodskill.skill.ApostleSkill;
import com.ecrea.epicfightgodskill.skill.AssassinSkill;
import com.ecrea.epicfightgodskill.skill.BlessingSkill;
import com.ecrea.epicfightgodskill.skill.FlyingSkill;
import com.ecrea.epicfightgodskill.skill.GodDodgeSkill;
import com.ecrea.epicfightgodskill.skill.GodReachSkill;
import com.ecrea.epicfightgodskill.skill.GodStepSkill;
import com.ecrea.epicfightgodskill.skill.GodskillSkill;
import com.ecrea.epicfightgodskill.skill.PerfectGuardSkill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mod(EpicFightGodskillMod.MODID)
@Mod.EventBusSubscriber(modid = EpicFightGodskillMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EpicFightGodskillMod {

    public static final String MODID = "epicfightgodskill";

    /** PASSIVEスロット一覧 (PASSIVE1〜50 まとめて走査用) */
    public static final SkillSlot[] ALL_PASSIVE_SLOTS = new SkillSlot[]{
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

    public static Skill GODSKILL;
    public static Skill ANGELSKILL;
    public static Skill APOSTLESKILL;
    public static Skill BLESSINGSKILL;
    public static Skill FLYINGSKILL;
    public static Skill GODDODGESKILL;
    public static Skill GODSTEPSKILL;
    public static Skill PERFECTGUARDSKILL;
    public static Skill GODREACHSKILL;
    public static Skill ASSASSINSKILL;

    private static final Map<UUID, Integer> GODSKILL_HEAL_TICKS     = new HashMap<>();
    private static final Map<UUID, Integer> ANGEL_HEAL_TICKS        = new HashMap<>();
    private static final Map<UUID, Integer> APOSTLE_HEAL_TICKS      = new HashMap<>();
    private static final Map<UUID, Integer> BLESSING_HEAL_TICKS     = new HashMap<>();
    private static final Map<UUID, Integer> FLYING_HEAL_TICKS       = new HashMap<>();
    private static final Map<UUID, Integer> GODDODGE_HEAL_TICKS     = new HashMap<>();
    private static final Map<UUID, Integer> GODSTEP_HEAL_TICKS      = new HashMap<>();
    private static final Map<UUID, Integer> PERFECTGUARD_HEAL_TICKS = new HashMap<>();
    private static final Map<UUID, Integer> GODREACH_HEAL_TICKS     = new HashMap<>();
    private static final Map<UUID, Integer> ASSASSIN_HEAL_TICKS     = new HashMap<>();

    private static final Map<UUID, Integer> GODDODGE_BOOST_TICKS = new HashMap<>();
    private static final Map<UUID, Integer> GODSTEP_BOOST_TICKS  = new HashMap<>();
    private static final Map<UUID, Integer> PERFECTGUARD_FOOD_TICKS = new HashMap<>();

    public EpicFightGodskillMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, ModSkillSlots.class);
        modBus.addListener(EpicFightGodskillMod::onSkillBuild);
    }

    public static void onSkillBuild(SkillBuildEvent event) {
        SkillBuildEvent.ModRegistryWorker worker = event.createRegistryWorker(MODID);

        GODSKILL = worker.build(
            "godskill",      GodskillSkill::new,     GodskillSkill.createGodskillBuilder());
        ANGELSKILL = worker.build(
            "angelskill",    AngelSkill::new,         AngelSkill.createAngelBuilder());
        APOSTLESKILL = worker.build(
            "apostleskill",  ApostleSkill::new,       ApostleSkill.createApostleBuilder());
        BLESSINGSKILL = worker.build(
            "blessing",      BlessingSkill::new,      BlessingSkill.createBlessingBuilder());
        FLYINGSKILL = worker.build(
            "flying",        FlyingSkill::new,        FlyingSkill.createFlyingBuilder());
        GODDODGESKILL = worker.build(
            "god_dodge",     GodDodgeSkill::new,      GodDodgeSkill.createGodDodgeBuilder());
        GODSTEPSKILL = worker.build(
            "god_step",      GodStepSkill::new,       GodStepSkill.createGodStepBuilder());
        PERFECTGUARDSKILL = worker.build(
            "perfect_guard", PerfectGuardSkill::new,  PerfectGuardSkill.createPerfectGuardBuilder());
        GODREACHSKILL = worker.build(
            "god_reach",     GodReachSkill::new,      GodReachSkill.createGodReachBuilder());
        ASSASSINSKILL = worker.build(
            "assassin",      AssassinSkill::new,      AssassinSkill.createAssassinBuilder());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side  == LogicalSide.CLIENT)  return;

        Player player = event.player;
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        UUID uuid = player.getUUID();
        boolean hasGodskill     = false;
        boolean hasAngel        = false;
        boolean hasApostle      = false;
        boolean hasBlessing     = false;
        boolean hasFlying       = false;
        boolean hasGodDodge     = false;
        boolean hasGodStep      = false;
        boolean hasPerfectGuard = false;
        boolean hasGodReach     = false;
        boolean hasAssassin     = false;

        // PASSIVE1〜50 を全走査
        for (SkillSlot slot : ALL_PASSIVE_SLOTS) {
            SkillContainer c = patch.getSkill(slot);
            if (c == null || c.getSkill() == null) continue;
            if (c.getSkill() == GODSKILL)      hasGodskill  = true;
            if (c.getSkill() == ANGELSKILL)    hasAngel     = true;
            if (c.getSkill() == APOSTLESKILL)  hasApostle   = true;
            if (c.getSkill() == GODREACHSKILL) hasGodReach  = true;
            if (c.getSkill() == ASSASSINSKILL) hasAssassin  = true;
        }

        // IDENTITY
        SkillContainer identity = patch.getSkill(SkillSlots.IDENTITY);
        if (identity != null && identity.getSkill() == BLESSINGSKILL) hasBlessing = true;

        // MOVER
        SkillContainer mover = patch.getSkill(SkillSlots.MOVER);
        if (mover != null && mover.getSkill() == FLYINGSKILL) hasFlying = true;

        // DODGE
        SkillContainer dodge = patch.getSkill(SkillSlots.DODGE);
        if (dodge != null) {
            if (dodge.getSkill() == GODDODGESKILL) hasGodDodge = true;
            if (dodge.getSkill() == GODSTEPSKILL)  hasGodStep  = true;
        }

        // GUARD
        SkillContainer guard = patch.getSkill(SkillSlots.GUARD);
        if (guard != null && guard.getSkill() == PERFECTGUARDSKILL) hasPerfectGuard = true;

        // ── GODSKILLのWEAPON_INNATEスタック常時維持 ─────────
        if (hasGodskill) {
            SkillContainer innate = patch.getSkill(SkillSlots.WEAPON_INNATE);
            if (innate != null && innate.getSkill() != null) {
                int max = innate.getSkill().getMaxStack();
                if (max > 0 && innate.getStack() < max) {
                    innate.getSkill().setStackSynchronize(innate, max);
                }
            }
        }

        // ── 飛行維持 ─────────────────────────────────────────
        if (player instanceof ServerPlayer sp && !sp.isCreative() && !sp.isSpectator()) {
            if (hasFlying && !sp.getAbilities().mayfly) {
                sp.getAbilities().mayfly = true;
                sp.getAbilities().flying = true;
                sp.onUpdateAbilities();
            } else if (!hasFlying && sp.getAbilities().mayfly) {
                sp.getAbilities().mayfly = false;
                sp.getAbilities().flying = false;
                sp.onUpdateAbilities();
            }
        }

        // ── 速度ブースト自動解除 ──────────────────────────────
        boostTick(player, uuid, hasGodDodge, GODDODGE_BOOST_TICKS, GodDodgeSkill::removeBoost);
        boostTick(player, uuid, hasGodStep,  GODSTEP_BOOST_TICKS,  GodStepSkill::removeBoost);

        // ── 体力回復 ─────────────────────────────────────────
        healTick(player, uuid, hasGodskill,     GODSKILL_HEAL_TICKS,     6.0f);
        healTick(player, uuid, hasAngel,        ANGEL_HEAL_TICKS,        3.0f);
        healTick(player, uuid, hasApostle,      APOSTLE_HEAL_TICKS,      2.0f);
        healTick(player, uuid, hasBlessing,     BLESSING_HEAL_TICKS,     5.0f);
        healTick(player, uuid, hasFlying,       FLYING_HEAL_TICKS,       3.0f);
        healTick(player, uuid, hasGodDodge,     GODDODGE_HEAL_TICKS,     3.0f);
        healTick(player, uuid, hasGodStep,      GODSTEP_HEAL_TICKS,      3.0f);
        healTick(player, uuid, hasPerfectGuard, PERFECTGUARD_HEAL_TICKS, 3.0f);
        healTick(player, uuid, hasGodReach,     GODREACH_HEAL_TICKS,     3.0f);
        healTick(player, uuid, hasAssassin,     ASSASSIN_HEAL_TICKS,     3.0f);

        // ── パーフェクトガード: ガード中の満腹度＋隠し満腹度回復 ──
        if (hasPerfectGuard) {
            SkillContainer guardC = patch.getSkill(SkillSlots.GUARD);
            if (guardC != null && guardC.isActivated()) {
                int ft = PERFECTGUARD_FOOD_TICKS.getOrDefault(uuid, 0) + 1;
                if (ft >= 20) { // 1秒ごと
                    FoodData food = player.getFoodData();
                    // 満腹度を6回復 (最大20)
                    food.setFoodLevel(Math.min(food.getFoodLevel() + 6, 20));
                    // 隠し満腹度を6回復 (最大は満腹度と同じ値)
                    food.setSaturation(Math.min(food.getSaturationLevel() + 6.0f, (float) food.getFoodLevel()));
                    ft = 0;
                }
                PERFECTGUARD_FOOD_TICKS.put(uuid, ft);
            } else {
                PERFECTGUARD_FOOD_TICKS.remove(uuid);
            }
        } else {
            PERFECTGUARD_FOOD_TICKS.remove(uuid);
        }
    }

    /**
     * LivingAttackEvent (HIGHEST) — 環境ダメージ (溶岩・落下・奈落など) をガード演出付きで無効化。
     * Epic Fight の TAKE_DAMAGE_EVENT_ATTACK では環境ダメージを拾えないため、
     * Forge側のイベントで補完する。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (sp.level().isClientSide()) return;

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(sp);
        if (patch == null) return;

        SkillContainer guard = patch.getSkill(SkillSlots.GUARD);
        if (guard == null || guard.getSkill() != PERFECTGUARDSKILL) return;
        if (!guard.isActivated()) return;

        // CLASH音を鳴らす
        patch.playSound(yesman.epicfight.gameasset.EpicFightSounds.CLASH.get(), -0.05F, 0.1F);

        // ガードアニメーション再生
        yesman.epicfight.world.capabilities.item.CapabilityItem itemCap =
                patch.getHoldingItemCapability(net.minecraft.world.InteractionHand.MAIN_HAND);
        try {
            java.lang.reflect.Method m = yesman.epicfight.skill.guard.GuardSkill.class.getDeclaredMethod(
                "getGuardMotion",
                yesman.epicfight.skill.SkillContainer.class,
                yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.class,
                yesman.epicfight.world.capabilities.item.CapabilityItem.class,
                yesman.epicfight.skill.guard.GuardSkill.BlockType.class
            );
            m.setAccessible(true);
            @SuppressWarnings("unchecked")
            var anim = (yesman.epicfight.api.animation.AnimationManager.AnimationAccessor<?
                    extends yesman.epicfight.api.animation.types.StaticAnimation>)
                    m.invoke(guard.getSkill(), guard, patch, itemCap,
                             yesman.epicfight.skill.guard.GuardSkill.BlockType.GUARD);
            if (anim != null) {
                patch.playAnimationSynchronized(anim, 0.0F);
            }
        } catch (Exception ignored) {}

        event.setCanceled(true);
    }

    /**
     * LivingHurtEvent — 環境ダメージがAttackEventを通過した場合のフォールバック。
     * ガード中なら全ダメージをキャンセル。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        SkillContainer guard = patch.getSkill(SkillSlots.GUARD);
        if (guard == null || guard.getSkill() != PERFECTGUARDSKILL) return;

        if (guard.isActivated()) {
            event.setCanceled(true);
        }
    }

    /**
     * LivingHealEvent — パーフェクトガードでガード中、全ての回復量を3倍にする。
     * (自然回復、ポーション、その他すべて)
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        SkillContainer guard = patch.getSkill(SkillSlots.GUARD);
        if (guard == null || guard.getSkill() != PERFECTGUARDSKILL) return;

        if (guard.isActivated()) {
            event.setAmount(event.getAmount() * 3.0f);
        }
    }

    public static void startGodDodgeBoost(UUID uuid) {
        GODDODGE_BOOST_TICKS.put(uuid, GodDodgeSkill.BOOST_TICKS);
    }

    public static void startGodStepBoost(UUID uuid) {
        GODSTEP_BOOST_TICKS.put(uuid, GodStepSkill.BOOST_TICKS);
    }

    private static void boostTick(Player player, UUID uuid, boolean hasSkill,
                                   Map<UUID, Integer> map, Consumer<Player> removeAction) {
        if (hasSkill) {
            int remaining = map.getOrDefault(uuid, 0);
            if (remaining > 0) {
                int next = remaining - 1;
                map.put(uuid, next);
                if (next <= 0) removeAction.accept(player);
            }
        } else if (map.containsKey(uuid)) {
            removeAction.accept(player);
            map.remove(uuid);
        }
    }

    private static void healTick(Player player, UUID uuid,
                                  boolean hasSkill, Map<UUID, Integer> map, float amount) {
        if (hasSkill) {
            int t = map.getOrDefault(uuid, 0) + 1;
            if (t >= 40) { player.heal(amount); t = 0; }
            map.put(uuid, t);
        } else {
            map.remove(uuid);
        }
    }
}
