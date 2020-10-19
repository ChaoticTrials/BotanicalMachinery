package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.helper.RecipeHelper2;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.recipe.IRuneAltarRecipe;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TileMechanicalRunicAltar extends TileBase implements IWorkingTile {

    public static final int MAX_MANA_PER_TICK = 100;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(33, slot -> {
        this.update = true;
        this.sendPacket = true;
    }, this::isValidStack);
    private IRuneAltarRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private int maxProgress;
    private boolean update = true;
    private final List<Integer> slotsUsed = new ArrayList<>();

    public TileMechanicalRunicAltar(TileEntityType<?> type) {
        super(type, ServerConfig.capacityRunicAltar.get());
        this.inventory.setInputSlots(IntStream.range(1, 17).toArray());
        this.inventory.setOutputSlots(IntStream.range(17, 33).toArray());
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (this.world == null) return false;
        if (slot == 0) return stack.getItem() == ModBlocks.livingrock.asItem();
        else if (Arrays.stream(this.inventory.getInputSlots()).anyMatch(x -> x == slot))
            return RecipeHelper.isItemValidInput(this.world.getRecipeManager(), ModRecipeTypes.RUNE_TYPE, stack);
        return true;
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            RecipeHelper2.removeFromList(stacks, IntStream.range(17, stacks.size() - 1).toArray(), new int[]{0});

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IRuneAltarRecipe) {
                    if (RecipeHelper.matches(recipe, stacks, false) && !this.inventory.getStackInSlot(0).isEmpty()) {
                        List<ItemStack> stacksToTest = new ArrayList<>();
                        stacksToTest.add(recipe.getRecipeOutput());
                        for (Ingredient ingredient : recipe.getIngredients()) {
                            for (ItemStack stack : this.inventory.getStacks()) {
                                if (ingredient.test(stack)) {
                                    if (ModTags.Items.RUNES.contains(stack.getItem())) {
                                        ItemStack rune = stack.copy();
                                        rune.setCount(1);
                                        for (ItemStack testStack : stacksToTest) {
                                            if (ItemHandlerHelper.canItemStacksStack(testStack, rune)) {
                                                testStack.grow(1);
                                                break;
                                            }
                                        }
                                        stacksToTest.add(rune);
                                        break;
                                    }
                                }
                            }
                        }
                        if (this.canInsertAll(stacksToTest)) {
                            this.recipe = (IRuneAltarRecipe) recipe;
                            this.slotsUsed.clear();
                            for (Ingredient ingredient : recipe.getIngredients()) {
                                for (int slot : this.inventory.getInputSlots()) {
                                    if (!this.slotsUsed.contains(slot) && ingredient.test(this.inventory.getStackInSlot(slot)))
                                        this.slotsUsed.add(slot);
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
        this.slotsUsed.clear();
        this.recipe = null;
    }

    @Override
    public boolean hasValidRecipe() {
        if (!this.inventory.isInputEmpty()) {
            return !this.inventory.getStackInSlot(0).isEmpty();
        }
        return true;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT cmp) {
        super.read(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.maxProgress = cmp.getInt(TileTags.MAX_PROGRESS);
        this.slotsUsed.clear();
        this.slotsUsed.addAll(Arrays.stream(cmp.getIntArray(TileTags.SLOTS_USED)).boxed().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT cmp) {
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        cmp.putIntArray(TileTags.SLOTS_USED, this.slotsUsed);
        return super.write(cmp);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT cmp) {
        if (world != null && !world.isRemote) return;
        super.handleUpdateTag(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.maxProgress = cmp.getInt(TileTags.MAX_PROGRESS);
        this.slotsUsed.clear();
        this.slotsUsed.addAll(Arrays.stream(cmp.getIntArray(TileTags.SLOTS_USED)).boxed().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        if (world != null && world.isRemote) return super.getUpdateTag();
        CompoundNBT cmp = super.getUpdateTag();
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        cmp.putIntArray(TileTags.SLOTS_USED, this.slotsUsed);
        return cmp;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world != null && !this.world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (this.recipe != null) {
                this.maxProgress = this.recipe.getManaUsage();
                int manaTransfer = Math.min(this.mana, Math.min(this.getMaxManaPerTick(), this.getMaxProgress() - this.progress));
                this.progress += manaTransfer;
                this.receiveMana(-manaTransfer);
                if (this.progress >= this.getMaxProgress()) {
                    ItemStack output = this.recipe.getRecipeOutput().copy();
                    for (Ingredient ingredient : this.recipe.getIngredients()) {
                        for (ItemStack stack : this.inventory.getStacks()) {
                            if (ingredient.test(stack)) {
                                if (ModTags.Items.RUNES.contains(stack.getItem())) {
                                    ItemStack rune = stack.copy();
                                    rune.setCount(1);
                                    this.putIntoOutputOrDrop(rune);
                                }
                                stack.shrink(1);
                                break;
                            }
                        }
                    }
                    this.inventory.getStackInSlot(0).shrink(1);
                    this.putIntoOutputOrDrop(output);
                    this.update = true;
                    done = true;
                }
                this.markDirty();
                this.markDispatchable();
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.maxProgress = -1;
                this.markDirty();
                this.markDispatchable();
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
        } else if (this.world != null && ClientConfig.everything.get() && ClientConfig.agglomerationFactory.get()) {
            if (this.getMaxProgress() > 0 && this.progress >= (this.getMaxProgress() - (5 * this.getMaxManaPerTick()))) {
                for (int i = 0; i < 5; ++i) {
                    SparkleParticleData data = SparkleParticleData.sparkle(this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), 10);
                    this.world.addParticle(data, this.pos.getX() + 0.3 + (this.world.rand.nextDouble() * 0.4), this.pos.getY() + 0.7, this.pos.getZ() + 0.3 + (this.world.rand.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    private void putIntoOutputOrDrop(ItemStack stack) {
        ItemStack leftToInsert = stack;
        for (int i : this.inventory.getOutputSlots()) {
            if (stack.isEmpty() || leftToInsert.isEmpty())
                break;
            leftToInsert = this.inventory.getUnrestricted().insertItem(i, leftToInsert, false);
        }
        //noinspection ConstantConditions
        if (!leftToInsert.isEmpty() && !this.world.isRemote) {
            ItemEntity ie = new ItemEntity(this.world, this.pos.getX() + 0.5, this.pos.getY() + 0.7, this.pos.getZ() + 0.5, leftToInsert.copy());
            this.world.addEntity(ie);
        }
    }

    private boolean canInsertAll(List<ItemStack> stacks) {
        int freeSlotsNeeded = 0;
        for (ItemStack stack : stacks) {
            freeSlotsNeeded += this.freeSlotsNeededFor(stack);
        }
        for (int i : this.inventory.getOutputSlots()) {
            ItemStack slotContent = this.inventory.getStackInSlot(i);
            if (slotContent.isEmpty()) {
                freeSlotsNeeded -= 1;
            }
        }
        return freeSlotsNeeded <= 0;
    }

    private int freeSlotsNeededFor(ItemStack stack) {
        int sizeLeft = stack.getCount();
        for (int i : this.inventory.getOutputSlots()) {
            ItemStack slotContent = this.inventory.getStackInSlot(i);
            if (slotContent.isEmpty())
                continue;
            if (ItemHandlerHelper.canItemStacksStack(stack, slotContent)) {
                sizeLeft -= Math.min(sizeLeft, slotContent.getMaxStackSize() - slotContent.getCount());
                if (sizeLeft <= 0)
                    return 0;
            }
        }
        return 1;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK / ServerConfig.multiplierRunicAltar.get();
    }

    public boolean isSlotUsedCurrently(int slot) {
        return this.slotsUsed.contains(slot);
    }
}
