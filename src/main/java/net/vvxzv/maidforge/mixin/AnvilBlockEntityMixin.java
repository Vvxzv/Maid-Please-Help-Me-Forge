package net.vvxzv.maidforge.mixin;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilBlockEntity.class)
public class AnvilBlockEntityMixin {
    @Shadow(remap = false)
    private @Nullable ResourceLocation lastRecipe;

    @Inject(
            method = "chooseRecipe",
            at = @At(value = "FIELD", target = "Lnet/dries007/tfc/common/blockentities/AnvilBlockEntity;lastRecipe:Lnet/minecraft/resources/ResourceLocation;", ordinal = 0),
            cancellable = true,
            remap = false
    )
    private void maidforge$forceLastRecipeNullOnAssign(ResourceLocation recipeId, CallbackInfo ci) {
        this.lastRecipe = null;
        ci.cancel();
    }

    @Inject(
            method = "setAndUpdateSlots",
            at = @At(value = "FIELD", target = "Lnet/dries007/tfc/common/blockentities/AnvilBlockEntity;lastRecipe:Lnet/minecraft/resources/ResourceLocation;", ordinal = 0),
            remap = false
    )
    private void maidforge$forceLastRecipeNullOnRead(int slot, CallbackInfo ci) {
        this.lastRecipe = null;
    }
    
    @ModifyArg(
            method = "saveAdditional",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0),
            index = 1,
            remap = false
    )
    private String maidforge$preventLastRecipeSave(String value) {
        return "";
    }

    @Inject(
            method = "loadAdditional",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z", ordinal = 0),
            cancellable = true,
            remap = false
    )
    private void maidforge$preventLastRecipeLoad(CompoundTag nbt, HolderLookup.Provider provider, CallbackInfo ci) {
        this.lastRecipe = null;
        ci.cancel();
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/dries007/tfc/common/blockentities/InventoryBlockEntity$InventoryFactory;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void maidforge$initLastRecipeToNull(CallbackInfo ci) {
        this.lastRecipe = null;
    }
}
