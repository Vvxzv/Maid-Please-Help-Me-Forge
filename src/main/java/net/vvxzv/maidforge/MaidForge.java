package net.vvxzv.maidforge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.vvxzv.maidforge.entity.memory.MemoryRegistry;
import net.vvxzv.maidforge.entity.sensor.SensorRegistry;

@Mod(MaidForge.MODID)
public class MaidForge {
    public static final String MODID = "maidforge";

    public MaidForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SensorRegistry.SENSOR.register(modEventBus);
        MemoryRegistry.MEMORY.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
