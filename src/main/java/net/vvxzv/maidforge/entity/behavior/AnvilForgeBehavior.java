package net.vvxzv.maidforge.entity.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blocks.devices.AnvilBlock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.vvxzv.maidforge.Config;
import net.vvxzv.maidforge.entity.memory.MemoryRegistry;
import net.vvxzv.maidforge.utils.ForgeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AnvilForgeBehavior extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = Config.maxDelayTimeToForge;
    private final float speed;
    private final int closeEnoughDist;
    private AnvilBlockEntity anvilBlockEntity;
    private static final GameProfile PROFILE = new GameProfile(UUID.randomUUID(), "Maid");

    private int getPerfectForgeFavorability(int stage){
        return Config.favorabilityToPerfectForge * stage / 4;
    }

    
    public AnvilForgeBehavior() {
        super(ImmutableMap.of(
                MemoryRegistry.ANVIL_TARGET.get(),
                MemoryStatus.VALUE_PRESENT,
                InitEntities.TARGET_POS.get(), 
                MemoryStatus.VALUE_ABSENT
        ));
        this.speed = 0.6F;
        this.closeEnoughDist = 2;
        this.setMaxCheckRate(MAX_DELAY_TIME);
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel worldIn, @NotNull EntityMaid maid) {
        if (super.checkExtraStartConditions(worldIn, maid) && maid.canBrainMoving()) {
            BlockPos anvilPos = this.findAnvil(worldIn, maid);
            if (anvilPos != null && maid.isWithinRestriction(anvilPos)) {
                if (anvilPos.distToCenterSqr(maid.position()) < Math.pow(closeEnoughDist, 2F)) {
                    maid.getBrain().setMemory(InitEntities.TARGET_POS.get(), new BlockPosTracker(anvilPos));
                    return true;
                }

                BehaviorUtils.setWalkAndLookTargetMemories(maid, anvilPos, speed, 2);
                this.setNextCheckTickCount(5);
            } else {
                maid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
            }
        }

        return false;
    }

    @Override
    protected void start(@NotNull ServerLevel serverLevel, EntityMaid entityMaid, long gameTimeIn) {
        entityMaid.getBrain().getMemory(MemoryRegistry.ANVIL_TARGET.get()).ifPresent((target) -> {
            BehaviorUtils.setWalkAndLookTargetMemories(entityMaid, target.getBlockPos(), speed, 2);
            this.anvilBlockEntity = target;
            forgeEvent(anvilBlockEntity, entityMaid);

        });
        entityMaid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
        entityMaid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        entityMaid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private void forgeEvent(AnvilBlockEntity anvilBlockEntity, EntityMaid entityMaid) {
        ItemStack hammer = entityMaid.getMainHandItem();
        if (!hammer.is(TFCTags.Items.HAMMERS)) return;

        if(!(anvilBlockEntity.getLevel() instanceof ServerLevel level)) return;

        ServerPlayer fakePlayer = createFakePlayer(level, hammer);

        Forging forging = anvilBlockEntity.getMainInputForging();
        if (forging == null || forging.getWorkTarget() == 0) return;

        AnvilRecipe recipe = forging.getRecipe(level);
        if (recipe == null) return;

        int currentWork = forging.getWork();
        int targetWork = forging.getWorkTarget();
        ForgeStep[] lastSteps = ForgeUtil.AdjustedForgeRule.autoLastSteps(recipe.getRules());
        int last = getStepValue(lastSteps, 0);
        int secondLast = getStepValue(lastSteps, 1);
        int thirdLast = getStepValue(lastSteps, 2);
        int delta = targetWork - last - secondLast - thirdLast - currentWork;

        if (delta == 0) {
            handleAlign(anvilBlockEntity, fakePlayer, entityMaid, lastSteps);
            return;
        }

        anvilWork(anvilBlockEntity, fakePlayer, ForgeUtil.findForgeStep(currentWork, delta), entityMaid);
    }

    private ServerPlayer createFakePlayer(ServerLevel level, ItemStack hammer) {
        ServerPlayer fakePlayer = new FakePlayer(level, PROFILE);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, hammer);
        return fakePlayer;
    }

    private int getStepValue(ForgeStep[] lastSteps, int index) {
        return index < lastSteps.length && lastSteps[index] != null ? lastSteps[index].step() : 0;
    }

    private void handleAlign(AnvilBlockEntity anvilBlockEntity, ServerPlayer player, EntityMaid entityMaid, ForgeStep[] lastSteps) {
        int favorability = entityMaid.getFavorability();
        double randomNum = Math.random();

        if (favorability < getPerfectForgeFavorability(4) && randomNum < 0.25) {
            anvilWork(anvilBlockEntity, player, ForgeStep.DRAW, entityMaid);
        } else if (favorability < getPerfectForgeFavorability(3) && randomNum < 0.4) {
            anvilWork(anvilBlockEntity, player, ForgeStep.HIT_HARD, entityMaid);
        } else if (favorability < getPerfectForgeFavorability(2) && randomNum < 0.55) {
            anvilWork(anvilBlockEntity, player, ForgeStep.HIT_MEDIUM, entityMaid);
        } else if (favorability < getPerfectForgeFavorability(1) && randomNum < 0.7) {
            anvilWork(anvilBlockEntity, player, ForgeStep.HIT_LIGHT, entityMaid);
        } else {
            if (getStepValue(lastSteps, 2) != 0) {
                anvilWork(anvilBlockEntity, player, lastSteps[2], entityMaid);
            }
            if (getStepValue(lastSteps, 1) != 0) {
                anvilWork(anvilBlockEntity, player, lastSteps[1], entityMaid);
            }
            if (getStepValue(lastSteps, 0) != 0) {
                anvilWork(anvilBlockEntity, player, lastSteps[0], entityMaid);
            }
        }
    }

    private BlockPos findAnvil(ServerLevel world, EntityMaid maid) {
        BlockPos centerPos = maid.getBrainSearchPos();
        int range = (int) maid.getRestrictRadius();

        BlockPos nearestAnvil = null;
        double minDistanceSquared = Double.MAX_VALUE;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos currentPos = centerPos.offset(x, y, z);
                    BlockState state = world.getBlockState(currentPos);
                    if (state.getBlock() instanceof AnvilBlock || state.getBlock() instanceof RockAnvilBlock) {
                        double distanceSquared = centerPos.distSqr(currentPos);
                        if (distanceSquared < minDistanceSquared) {
                            minDistanceSquared = distanceSquared;
                            nearestAnvil = currentPos;
                        }
                    }
                }
            }
        }
        return nearestAnvil;
    }

    private static void anvilWork(AnvilBlockEntity anvilBlockEntity, ServerPlayer player, ForgeStep step, EntityMaid entityMaid){
        if(anvilBlockEntity.work(player, step).shouldSwing()){
            entityMaid.swing(InteractionHand.MAIN_HAND);
        }
    }
}
