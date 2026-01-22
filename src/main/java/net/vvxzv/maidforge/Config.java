package net.vvxzv.maidforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = MaidForge.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue PERFECT_FORGE_FAVORABILITY = BUILDER.comment(" ").comment("The maid's favorability required for 100% perfect forging").comment("Default 40").comment("100%完美锻造所需的女仆好感度").defineInRange("perfectForgeFavorability", 40, 0, 100);

    private static final ModConfigSpec.IntValue FORGE_MAX_DELAY_TIME = BUILDER.comment(" ").comment("The max maid forge delay time").comment("Default 10").comment("女仆锻造最长延时").defineInRange("maxDelayTimeToForge", 10, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int perfectForgeFavorability;
    public static int maxDelayTimeToForge;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        perfectForgeFavorability = PERFECT_FORGE_FAVORABILITY.get();
        maxDelayTimeToForge = FORGE_MAX_DELAY_TIME.get();
    }
}
