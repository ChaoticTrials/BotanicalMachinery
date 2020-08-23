package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import vazkii.botania.api.recipe.IRuneAltarRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.material.ItemRune;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.IntStream;

public class TileMechanicalRunicAltar extends TileBase {

    public static final int WORKING_DURATION = 100;
    public static final String TAG_PROGRESS = "progress";

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(33, slot -> this.update = true, this::isValidStack);
    private IRuneAltarRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update = true;

    public TileMechanicalRunicAltar() {
        super(Registration.TILE_MECHANICAL_RUNIC_ALTAR.get(), 500_000);
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
        if (slot == 0) return stack.getItem() == ModBlocks.livingrock.asItem();
        else if (Arrays.stream(this.inventory.getInputSlots()).anyMatch(x -> x == slot))
            return RecipeHelper.isItemValid(this.world, ModRecipeTypes.RUNE_TYPE, stack);
        return true;
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.subList(17, stacks.size() - 1).clear();
            stacks.remove(0);
            Map<Item, Integer> items = new HashMap<>();
            stacks.removeIf(stack -> stack.getItem() == Blocks.AIR.asItem());
            stacks.forEach(stack -> {
                Item item = stack.getItem();
                if (!items.containsKey(item)) {
                    items.put(item, stack.getCount());
                } else {
                    int prevCount = items.get(item);
                    items.replace(item, prevCount, prevCount + stack.getCount());
                }
            });

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IRuneAltarRecipe) {
                    if (RecipeHelper.checkIngredients(stacks, items, recipe) && !this.inventory.getStackInSlot(0).isEmpty()) {
                        this.recipe = (IRuneAltarRecipe) recipe;
                        return;
                    }
                }
            }
        }
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
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);
        cmp.putInt(TAG_PROGRESS, this.progress);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInt(TAG_PROGRESS);
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
                if (this.getCurrentMana() >= this.recipe.getManaUsage() || this.progress > 0 && this.progress <= WORKING_DURATION) {
                    ++this.progress;
                    this.receiveMana(-(this.recipe.getManaUsage() / WORKING_DURATION));
                    if (this.progress >= WORKING_DURATION) {
                        ItemStack output = this.recipe.getRecipeOutput().copy();
                        for (Ingredient ingredient : this.recipe.getIngredients()) {
                            for (ItemStack stack : this.inventory.getStacks()) {
                                if (ingredient.test(stack)) {
                                    if (stack.getItem() instanceof ItemRune) {
                                        ItemStack rune = stack.copy();
                                        rune.setCount(1);
                                        this.putIntoOutput(rune);
                                    }
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                        this.inventory.getStackInSlot(0).shrink(1);
                        this.putIntoOutput(output);
                        this.update = true;
                        done = true;
                    }
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
        }
    }

    private void putIntoOutput(ItemStack stack) {
        for (int i : this.inventory.getOutputSlots()) {
            if (stack.isEmpty()) break;
            ItemStack slotStack = this.inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                this.inventory.getUnrestricted().insertItem(i, stack.copy(), false);
                break;
            } else if ((slotStack.getItem() == stack.getItem() && slotStack.getCount() < slotStack.getMaxStackSize())) {
                ItemStack left = this.inventory.getUnrestricted().insertItem(i, stack, false);
                if (left != ItemStack.EMPTY) stack = left;
                else break;
            }
        }
    }

    public int getProgress() {
        return this.progress;
    }
}
