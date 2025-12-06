package net.vvxzv.maidforge.entity.sensor;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.maidforge.entity.memory.MemoryRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ForgeSensor extends Sensor<EntityMaid> {
    private static final int scanRate = 100;

    public ForgeSensor() {
        super(scanRate);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, EntityMaid entityMaid) {
        AnvilBlockEntity anvilBlockEntity = null;
        BlockPos pos = entityMaid.getOnPos();

        for (int i = 0; i <= entityMaid.getTask().searchRadius(entityMaid); ++i){
            AnvilBlockEntity searchResult = this.searchAnvilBlockEntity(serverLevel, pos, i);
            if(searchResult != null){
                anvilBlockEntity = searchResult;
                break;
            }
        }

        entityMaid.getBrain().setMemory(MemoryRegistry.ANVIL_TARGET.get(), anvilBlockEntity);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryRegistry.ANVIL_TARGET.get());
    }

    private @Nullable AnvilBlockEntity searchAnvilBlockEntity(ServerLevel serverLevel, Vec3i center, int radius) {
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();
        int xMin = x - radius;
        int xMax = x + radius;
        int yMin = y - radius;
        int yMax = y + radius;
        int zMin = z - radius;
        int zMax = z + radius;

        for(int i = xMin; i < xMax; ++i) {
            for(int j = yMin; j < yMax; ++j) {
                BlockPos pos = new BlockPos(i, j, zMin);
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                    if (blockEntity instanceof AnvilBlockEntity anvilBlockEntity) {
                        return anvilBlockEntity;
                    }
                }
            }
        }

        for(int i = zMin; i < zMax; ++i) {
            for(int j = yMin; j < yMax; ++j) {
                BlockPos pos = new BlockPos(xMax, j, i);
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                    if (blockEntity instanceof AnvilBlockEntity anvilBlockEntity) {
                        return anvilBlockEntity;
                    }
                }
            }
        }

        for(int i = xMax; i > xMin; --i) {
            for(int j = yMin; j < yMax; ++j) {
                BlockPos pos = new BlockPos(i, j, zMax);
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                    if (blockEntity instanceof AnvilBlockEntity anvilBlockEntity) {
                        return anvilBlockEntity;
                    }
                }
            }
        }

        for(int i = zMax; i > zMin; --i) {
            for(int j = yMin; j < yMax; ++j) {
                BlockPos pos = new BlockPos(xMin, j, i);
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                    if (blockEntity instanceof AnvilBlockEntity anvilBlockEntity) {
                        return anvilBlockEntity;
                    }
                }
            }
        }

        for(int i = xMin; i <= xMax; ++i) {
            for(int j = zMin; j <= zMax; ++j) {
                BlockPos pos = new BlockPos(i, yMax, j);
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                    if (blockEntity instanceof AnvilBlockEntity anvilBlockEntity) {
                        return anvilBlockEntity;
                    }
                }
            }
        }

        for(int i = xMin + 1; i < xMax; ++i) {
            for(int j = zMin + 1; j < zMax; ++j) {
                BlockPos pos = new BlockPos(i, yMin, j);
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                    if (blockEntity instanceof AnvilBlockEntity anvilBlockEntity) {
                        return anvilBlockEntity;
                    }
                }
            }
        }

        return null;
    }
}
