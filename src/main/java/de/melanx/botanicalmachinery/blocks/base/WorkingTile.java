package de.melanx.botanicalmachinery.blocks.base;

import de.melanx.botanicalmachinery.core.TileTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public abstract class WorkingTile<T extends Recipe<Container>> extends RecipeTile<T> {

    private int progress;
    private int maxProgress;
    
    public WorkingTile(BlockEntityType<?> blockEntityType, RecipeType<T> recipeType, BlockPos pos, BlockState state, int manaCap, int firstInputSlot, int firstOutputSlot) {
        super(blockEntityType, recipeType, pos, state, manaCap, firstInputSlot, firstOutputSlot);
        this.progress = 0;
        this.maxProgress = 0;
    }

    protected void runRecipeTick() {
        runRecipeTick(() -> {}, (stack, slot) -> {}, (stack, slot) -> {});
    }

    protected void runRecipeTick(Runnable doUpdate, BiConsumer<ItemStack, Integer> updateStack, BiConsumer<ItemStack, Integer> consumeStack) {
        if (this.level != null && !this.level.isClientSide) {
            this.updateRecipeIfNeeded(doUpdate, updateStack);
            if (this.recipe != null) {
                int newMaxProgress = this.getMaxProgress(this.recipe);
                if (newMaxProgress != this.maxProgress) {
                    this.maxProgress = newMaxProgress;
                    this.setChanged();
                    this.setDispatchable();
                }
                this.progress += this.getAndApplyProgressThisTick();
                if (this.progress >= this.getMaxProgress(this.recipe)) {
                    this.progress = 0;
                    this.craftRecipe(consumeStack);
                }
                this.setChanged();
                this.setDispatchable();
            } else if (this.progress != 0 || this.maxProgress != 0) {
                this.progress = 0;
                this.maxProgress = 0;
                this.setChanged();
                this.setDispatchable();
            }
        }
    }

    public int getProgress() {
        return this.progress;
    }
    
    public int getMaxProgress() {
        return this.maxProgress;
    }
    
    protected int getAndApplyProgressThisTick() {
        int manaToTransfer = Math.min(Math.min(this.getCurrentMana(), this.getMaxManaPerTick()), this.getMaxProgress(this.recipe) - this.progress);
        this.receiveMana(-manaToTransfer);
        return manaToTransfer;
    }

    protected abstract int getMaxProgress(T recipe);

    public abstract int getMaxManaPerTick();

    @Override
    public int getComparatorOutput() {
        return this.getProgress() > 0 ? 15 : 0;
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getInt(TileTags.PROGRESS);
        this.maxProgress = nbt.getInt(TileTags.MAX_PROGRESS);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt(TileTags.PROGRESS, this.progress);
        nbt.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        nbt.putInt(TileTags.PROGRESS, this.progress);
        nbt.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (this.level != null && !this.level.isClientSide) return;
        this.progress = tag.getInt(TileTags.PROGRESS);
        this.maxProgress = tag.getInt(TileTags.MAX_PROGRESS);
    }
}
