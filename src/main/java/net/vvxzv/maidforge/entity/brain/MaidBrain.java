package net.vvxzv.maidforge.entity.brain;

import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.vvxzv.maidforge.entity.memory.MemoryRegistry;
import net.vvxzv.maidforge.entity.sensor.SensorRegistry;

import java.util.ArrayList;
import java.util.List;

public class MaidBrain implements IExtraMaidBrain {
    @Override
    public List<MemoryModuleType<?>> getExtraMemoryTypes() {
        List<MemoryModuleType<?>> list = new ArrayList();
        list.add(MemoryRegistry.ANVIL_TARGET.get());
        return list;
    }

    @Override
    public List<SensorType<? extends Sensor<? super EntityMaid>>> getExtraSensorTypes() {
        List<SensorType<? extends Sensor<? super EntityMaid>>> list = new ArrayList();
        list.add(SensorRegistry.ANVIL_FORGE_SENSOR.get());
        return list;
    }
}
