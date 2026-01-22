package net.vvxzv.maidforge.entity.memory;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.maidforge.MaidForge;

import java.util.Optional;
import java.util.function.Supplier;

public class MemoryRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MaidForge.MODID);

    public static Supplier<MemoryModuleType<AnvilBlockEntity>> ANVIL_TARGET = MEMORY.register("anvil_target", () -> new MemoryModuleType<>(Optional.empty()));
}
