package net.vvxzv.maidforge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MaidForge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue PERFECT_FORGE_FAVORABILITY = BUILDER.comment(" ").comment("The maid's favorability required for 100% perfect forging").comment("Default 40").comment("100%完美锻造所需的女仆好感度").defineInRange("perfectForgeFavorability", 40, 0, 100);

    private static final ForgeConfigSpec.IntValue FORGE_MAX_DELAY_TIME = BUILDER.comment(" ").comment("The max maid forge delay time").comment("Default 20").comment("女仆锻造最长延时").defineInRange("forgeMaxDelayTime", 20, 0, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int perfectForgeFavorability;
    public static int forgeMaxDelayTime;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        perfectForgeFavorability = PERFECT_FORGE_FAVORABILITY.get();
        forgeMaxDelayTime = FORGE_MAX_DELAY_TIME.get();
    }
}
