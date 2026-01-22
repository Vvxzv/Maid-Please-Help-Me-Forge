package net.vvxzv.maidforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.vvxzv.maidforge.entity.memory.MemoryRegistry;
import net.vvxzv.maidforge.entity.sensor.SensorRegistry;

@Mod(MaidForge.MODID)
public class MaidForge {
    public static final String MODID = "maidforge";

    public MaidForge(IEventBus modEventBus, ModContainer modContainer) {
        SensorRegistry.SENSOR.register(modEventBus);
        MemoryRegistry.MEMORY.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
