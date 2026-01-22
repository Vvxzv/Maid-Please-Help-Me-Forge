package net.vvxzv.maidforge.entity.sensor;

import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.maidforge.MaidForge;

public class SensorRegistry {
    public static final DeferredRegister<SensorType<?>> SENSOR = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MaidForge.MODID);

    public static RegistryObject<SensorType<ForgeSensor>> ANVIL_FORGE_SENSOR = SENSOR.register("anvil_forge_sensor", () -> new SensorType<>(ForgeSensor::new));
}
