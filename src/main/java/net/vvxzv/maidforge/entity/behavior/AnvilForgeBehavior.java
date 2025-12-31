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
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
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

import java.util.UUID;

public class AnvilForgeBehavior extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = Config.forgeMaxDelayTime;
    private final float speed;
    private final int closeEnoughDist;
    private AnvilBlockEntity anvilBlockEntity;
    private static final UUID uuid = UUID.randomUUID();

    private int getPerfectForgeFavorability(int stage){
        return Config.perfectForgeFavorability * stage / 4;
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
    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid maid) {
        if (super.checkExtraStartConditions(worldIn, maid) && maid.canBrainMoving()) {
            BlockPos anvilPos = this.findAnvil(worldIn, maid);
            if (anvilPos != null && maid.isWithinRestriction(anvilPos)) {
                if (anvilPos.distToCenterSqr(maid.position()) < Math.pow(closeEnoughDist, 2.0F)) {
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
    protected void start(ServerLevel serverLevel, EntityMaid entityMaid, long gameTimeIn) {
        entityMaid.getBrain().getMemory(MemoryRegistry.ANVIL_TARGET.get()).ifPresent((target) -> {
            BehaviorUtils.setWalkAndLookTargetMemories(entityMaid, target.getBlockPos(), speed, 2);
            this.anvilBlockEntity = target;
            forgeEvent(anvilBlockEntity, entityMaid);

        });
        entityMaid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
        entityMaid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        entityMaid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private void forgeEvent(AnvilBlockEntity anvilBlockEntity, EntityMaid entityMaid){
        ItemStack hammer = entityMaid.getMainHandItem();
        if(!hammer.is(TFCTags.Items.HAMMERS)) return;

        ServerPlayer player = new FakePlayer((ServerLevel) anvilBlockEntity.getLevel(), new GameProfile(uuid, "maid_forge"));
        player.setItemInHand(InteractionHand.MAIN_HAND, hammer);

        Forging forging = anvilBlockEntity.getMainInputForging();
        if (forging != null) {
            int currentWork = forging.getWork();
            int targetWork = forging.getWorkTarget();
            if (targetWork == 0) {
                return;
            }
            AnvilRecipe recipe = forging.getRecipe(anvilBlockEntity.getLevel());
            if (recipe != null) {
                entityMaid.swing(InteractionHand.MAIN_HAND);

                ForgeRule[] rules = recipe.getRules();
                ForgeStep[] lastSteps = ForgeUtil.AdjustedForgeRule.autoLastSteps(rules);

                int last = lastSteps[0] != null? lastSteps[0].step(): 0;
                int secondLast = lastSteps[1] != null? lastSteps[1].step(): 0;
                int thirdLast = lastSteps[2] != null? lastSteps[2].step(): 0;
                int delta = targetWork - last - secondLast - thirdLast - currentWork;

                if(delta == 0){
                    int Favorability = entityMaid.getFavorability();
                    double randomNum = Math.random();
                    if(Favorability < getPerfectForgeFavorability(4) && randomNum < 0.1){
                        anvilBlockEntity.work(player, ForgeStep.DRAW);
                        return;
                    }
                    if(Favorability < getPerfectForgeFavorability(3) && randomNum < 0.2){
                        anvilBlockEntity.work(player, ForgeStep.HIT_HARD);
                        return;
                    }
                    if(Favorability < getPerfectForgeFavorability(2) && randomNum < 0.3){
                        anvilBlockEntity.work(player, ForgeStep.HIT_MEDIUM);
                        return;
                    }
                    if(Favorability < getPerfectForgeFavorability(1) && randomNum < 0.4){
                        anvilBlockEntity.work(player, ForgeStep.HIT_LIGHT);
                        return;
                    }
                    if(thirdLast != 0) {
                        anvilBlockEntity.work(player, lastSteps[2]);
                    }
                    if(secondLast != 0) {
                        anvilBlockEntity.work(player, lastSteps[1]);
                    }
                    if(last != 0) {
                        anvilBlockEntity.work(player, lastSteps[0]);
                    }
                }
                else if(delta == -3){
                    // -3
                    anvilBlockEntity.work(player, ForgeStep.HIT_LIGHT);
                }
                else if(delta == -6){
                    // -6
                    anvilBlockEntity.work(player, ForgeStep.HIT_MEDIUM);
                }
                else if(delta == -9){
                    // -9
                    anvilBlockEntity.work(player, ForgeStep.HIT_HARD);
                }
                else if(delta < 0){
                    // -15
                    anvilBlockEntity.work(player, ForgeStep.DRAW);
                }
                else if(delta == 40){
                    // 13 + 13 + 7 + 7
                    // 13 + [27]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 33){
                    // 13 + 13 + 7
                    // 13 + [20]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 28){
                    // 13 + 13 + 2
                    // 13 + [15]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 27){
                    // 13 + 7 + 7
                    // 13 + [14]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 26){
                    // 13 + 13
                    // 13 + [13]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 22){
                    // 13 + 7 + 2
                    // 13 + [9]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 21){
                    // 7 + 7 + 7
                    // 7 + [14]
                    anvilBlockEntity.work(player, ForgeStep.BEND);
                }
                else if(delta == 20){
                    // 13 + 7
                    // 13 + [7]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta >= 16){
                    // 16
                    anvilBlockEntity.work(player, ForgeStep.SHRINK);
                }
                else if(delta == 15){
                    // 13 + 2
                    // 13 + [2]
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 14){
                    // 7 + 7
                    // 7 + [7]
                    anvilBlockEntity.work(player, ForgeStep.BEND);
                }
                else if(delta == 13){
                    // 13
                    anvilBlockEntity.work(player, ForgeStep.UPSET);
                }
                else if(delta == 12){
                    // -3 + 13 + 2
                    // -3 + [15]
                    anvilBlockEntity.work(player, ForgeStep.HIT_LIGHT);
                }
                else if(delta == 11){
                    // 7 + 2 + 2
                    // 7 + [4]
                    anvilBlockEntity.work(player, ForgeStep.BEND);
                }
                else if(delta == 10){
                    // -6 + 16
                    // -6 + [16]
                    anvilBlockEntity.work(player, ForgeStep.HIT_MEDIUM);
                }
                else if(delta == 9){
                    // 7 + 2
                    // 7 + [2]
                    anvilBlockEntity.work(player, ForgeStep.BEND);
                }
                else if(delta == 8){
                    // -6 + 7 + 7
                    // -6 + [14]
                    anvilBlockEntity.work(player, ForgeStep.HIT_MEDIUM);
                }
                else if(delta == 7){
                    // 7
                    anvilBlockEntity.work(player, ForgeStep.BEND);
                }
                else if(delta == 6){
                    // 2 + 2 + 2
                    // 2 + [4]
                    anvilBlockEntity.work(player, ForgeStep.PUNCH);
                }
                else if(delta == 5){
                    // -6 + 7 + 2 + 2
                    // -6 + [11]
                    anvilBlockEntity.work(player, ForgeStep.HIT_MEDIUM);
                }
                else if(delta == 4){
                    // 2 + 2
                    // 2 + [2]
                    anvilBlockEntity.work(player, ForgeStep.PUNCH);
                }
                else if(delta == 3){
                    // -6 + 7 + 2
                    // -6 + [9]
                    anvilBlockEntity.work(player, ForgeStep.BEND);
                }
                else if(delta == 2){
                    // 2
                    anvilBlockEntity.work(player, ForgeStep.PUNCH);
                }
                else {
                    // delta == 1
                    // -15 + 16
                    // -15 + [16]
                    anvilBlockEntity.work(player, ForgeStep.DRAW);
                }
            }
        }
    }

    private BlockPos findAnvil(ServerLevel world, EntityMaid maid){
        BlockPos blockPos = maid.getBrainSearchPos();
        int range = (int) maid.getRestrictRadius();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos currentPos = blockPos.offset(x, y, z);
                    BlockState state = world.getBlockState(currentPos);
                    if (state.getBlock() instanceof AnvilBlock || state.getBlock() instanceof RockAnvilBlock) {
                        return currentPos;
                    }
                }
            }
        }
        return null;
    }
}
