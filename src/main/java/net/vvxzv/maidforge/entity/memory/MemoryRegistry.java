package net.vvxzv.maidforge.entity.memory;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.maidforge.MaidForge;

import java.util.Optional;

public class MemoryRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY;
    public static RegistryObject<MemoryModuleType<AnvilBlockEntity>> ANVIL_TARGET;

    static {
        MEMORY = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MaidForge.MODID);
        ANVIL_TARGET = MEMORY.register("anvil_target", () -> new MemoryModuleType<>(Optional.empty()));
    }
}
