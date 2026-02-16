package net.vvxzv.maidforge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MaidForge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue FAVORABILITY_TO_PERFECT_FORGE = BUILDER.comment(" ").comment("The maid's favorability required for 100% perfect forging").comment("Default 64").comment("100%完美锻造所需的女仆好感度").defineInRange("favorabilityToPerfectForge", 64, 0, 384);

    private static final ForgeConfigSpec.IntValue FORGE_MAX_DELAY_TIME = BUILDER.comment(" ").comment("The max maid forge delay time").comment("Default 10").comment("女仆锻造最长延时").defineInRange("maxDelayTimeToForge", 10, 0, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int favorabilityToPerfectForge;
    public static int maxDelayTimeToForge;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        favorabilityToPerfectForge = FAVORABILITY_TO_PERFECT_FORGE.get();
        maxDelayTimeToForge = FORGE_MAX_DELAY_TIME.get();
    }
}
