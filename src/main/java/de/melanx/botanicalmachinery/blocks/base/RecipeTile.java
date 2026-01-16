package de.melanx.botanicalmachinery.blocks.base;

import de.melanx.botanicalmachinery.config.LibXServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.crafting.RecipeHelper;
import org.moddingx.libx.inventory.IAdvancedItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public abstract class RecipeTile<T extends Recipe<Container>> extends BotanicalTile {

    private final RecipeType<T> recipeType;
    private final int firstInputSlot;
    private final int firstOutputSlot;

    protected T recipe;
    private boolean needsRecipeUpdate;

    public RecipeTile(BlockEntityType<?> blockEntityType, RecipeType<T> recipeType, BlockPos pos, BlockState state, int manaCap, int firstInputSlot, int firstOutputSlot) {
        super(blockEntityType, pos, state, manaCap);
        this.recipeType = recipeType;
        this.firstInputSlot = firstInputSlot;
        this.firstOutputSlot = firstOutputSlot;
        this.needsRecipeUpdate = true;
    }

    protected void updateRecipeIfNeeded() {
        this.updateRecipeIfNeeded(() -> {}, (stack, slot) -> {});
    }

    protected void updateRecipeIfNeeded(Runnable doUpdate, BiConsumer<ItemStack, Integer> usedStacks) {
        if (this.level == null || this.level.isClientSide) return;
        if (this.needsRecipeUpdate) {
            this.needsRecipeUpdate = false;
            doUpdate.run();
            this.updateRecipe(usedStacks);
        }
    }

    protected void updateRecipe() {
        this.updateRecipe((stack, slot) -> {});
    }

    protected void updateRecipe(BiConsumer<ItemStack, Integer> usedStacks) {
        if (this.level == null || this.level.isClientSide) return;
        if (!this.canMatchRecipes()) {
            this.recipe = null;
            return;
        }
        IAdvancedItemHandlerModifiable inventory = this.getInventory().getUnrestricted();
        List<ItemStack> stacks = IntStream.range(this.firstInputSlot, this.firstOutputSlot).mapToObj(inventory::getStackInSlot).toList();
        for (T recipe : this.level.getRecipeManager().getAllRecipesFor(this.recipeType)) {
            if (this.matchRecipe(recipe, stacks)) {
                List<ItemStack> consumedStacks = new ArrayList<>();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (int stackIdx = 0; stackIdx < stacks.size(); stackIdx += 1) {
                        if (ingredient.test(stacks.get(stackIdx))) {
                            ItemStack theStack = stacks.get(stackIdx).copy();
                            theStack.setCount(1);
                            consumedStacks.add(theStack.copy());
                            usedStacks.accept(theStack, this.firstInputSlot + stackIdx);
                            break;
                        }
                    }
                }
                List<ItemStack> resultItems = this.resultItems(recipe, consumedStacks);
                if (resultItems.isEmpty() || inventory.hasSpaceFor(resultItems, this.firstOutputSlot, inventory.getSlots())) {
                    this.recipe = recipe;
                } else {
                    this.recipe = null;
                }
                return;
            }
        }
        this.recipe = null;
    }

    protected void craftRecipe() {
        this.craftRecipe((stack, slot) -> {});
    }

    protected void craftRecipe(BiConsumer<ItemStack, Integer> usedStacks) {
        if (this.level == null || this.level.isClientSide) return;
        if (this.recipe != null) {
            IAdvancedItemHandlerModifiable inventory = this.getInventory().getUnrestricted();
            List<ItemStack> consumedStacks = new ArrayList<>();
            for (Ingredient ingredient : this.recipe.getIngredients()) {
                for (int slot = this.firstInputSlot; slot < this.firstOutputSlot; slot++) {
                    if (ingredient.test(inventory.getStackInSlot(slot))) {
                        ItemStack extracted = inventory.extractItem(slot, 1, false);
                        if (!extracted.isEmpty()) {
                            consumedStacks.add(extracted.copy());
                            usedStacks.accept(extracted, slot);
                        }
                        break;
                    }
                }
            }
            for (ItemStack result : this.resultItems(this.recipe, consumedStacks)) {
                this.putIntoOutputOrDrop(result.copy());
            }
            this.onCrafted(this.recipe);
            this.recipe = null;
            this.needsRecipeUpdate();
        }
    }

    protected boolean canMatchRecipes() {
        return true;
    }

    // May not modify the stacks
    protected boolean matchRecipe(T recipe, List<ItemStack> stacks) {
        return RecipeHelper.matches(recipe, stacks, false);
    }

    protected void onCrafted(T recipe) {

    }

    // May not modify the stacks
    protected List<ItemStack> resultItems(T recipe, List<ItemStack> stacks) {
        //noinspection DataFlowIssue
        ItemStack stack = recipe.getResultItem(this.level.registryAccess());
        if (stack.isEmpty()) return List.of();
        return List.of(stack.copy());
    }

    protected void putIntoOutputOrDrop(ItemStack stack) {
        if (this.level == null || this.level.isClientSide) return;
        IAdvancedItemHandlerModifiable inventory = this.getInventory().getUnrestricted();
        ItemStack left = stack.copy();
        for (int slot = this.firstOutputSlot; slot < inventory.getSlots(); slot++) {
            left = inventory.insertItem(slot, left, false);
            if (left.isEmpty()) return;
        }
        if (!left.isEmpty()) {
            ItemEntity ie = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.7, this.worldPosition.getZ() + 0.5, left.copy());
            this.level.addFreshEntity(ie);
        }
    }

    protected void tryPushToAdjacentInventory() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        if (!this.hasItemsInOutputSlots()) {
            return;
        }

        List<IItemHandler> itemHandlers = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(dir));
            if (blockEntity == null) {
                continue;
            }

            if (!LibXServerConfig.autoExportAllowed.test(ForgeRegistries.BLOCKS.getKey(blockEntity.getBlockState().getBlock()))) {
                continue;
            }

            LazyOptional<IItemHandler> capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite());
            //noinspection DataFlowIssue
            IItemHandler itemHandler = capability.orElse(null);
            //noinspection ConstantValue
            if (itemHandler == null) {
                continue;
            }

            itemHandlers.add(itemHandler);
        }

        IAdvancedItemHandlerModifiable inventory = this.getInventory().getUnrestricted();
        for (int i = this.firstOutputSlot; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            for (IItemHandler itemHandler : itemHandlers) {
                stack = ItemHandlerHelper.insertItem(itemHandler, stack, false);
                if (stack.isEmpty()) {
                    break;
                }
            }

            inventory.setStackInSlot(i, stack);
        }
    }

    protected boolean hasItemsInOutputSlots() {
        IAdvancedItemHandlerModifiable inv = this.getInventory().getUnrestricted();
        int slots = inv.getSlots();
        for (int i = this.firstOutputSlot; i < slots; i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void needsRecipeUpdate() {
        this.needsRecipeUpdate = true;
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.needsRecipeUpdate = true;
    }
}
