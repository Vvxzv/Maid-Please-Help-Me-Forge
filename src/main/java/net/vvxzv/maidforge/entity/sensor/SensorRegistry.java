package net.vvxzv.maidforge.entity.sensor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.maidforge.MaidForge;

import java.util.function.Supplier;

public class SensorRegistry {
    public static final DeferredRegister<SensorType<?>> SENSOR = DeferredRegister.create(Registries.SENSOR_TYPE, MaidForge.MODID);

    public static Supplier<SensorType<ForgeSensor>> ANVIL_FORGE_SENSOR = SENSOR.register("anvil_forge_sensor", () -> new SensorType<>(ForgeSensor::new));
}
