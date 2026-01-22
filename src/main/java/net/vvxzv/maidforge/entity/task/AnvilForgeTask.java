package net.vvxzv.maidforge.entity.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.vvxzv.maidforge.MaidForge;
import net.vvxzv.maidforge.entity.behavior.AnvilForgeBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class AnvilForgeTask implements IMaidTask {
    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(MaidForge.MODID, "anvil_forge_task");
    }

    @Override
    public ItemStack getIcon() {
        return TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HAMMER).get().getDefaultInstance();
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return SoundUtil.environmentSound(entityMaid, InitSounds.MAID_IDLE.get(), 0.5F);
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        return Lists.newArrayList(new Pair[]{Pair.of(5, new AnvilForgeBehavior())});
    }

    @Override
    public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
        return Lists.newArrayList(Pair.of("has_hammer", this::hasHammer));
    }

    private boolean hasHammer(EntityMaid maid){
        return maid.getMainHandItem().is(TFCTags.Items.TOOLS_HAMMER);
    }
}
