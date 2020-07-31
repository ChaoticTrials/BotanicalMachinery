package de.melanx.botanicalmachinery.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
 * Thanks to Cucumber by BlakeBr0
 * https://github.com/BlakeBr0/Cucumber/blob/1.15/src/main/java/com/blakebr0/cucumber/inventory/BaseItemStackHandler.java
 */
public class BaseItemStackHandler extends ItemStackHandler {

    private final Function<Integer, Void> onContentsChanged;
    private final Map<Integer, Integer> slotSizeMap;
    private BiFunction<Integer, ItemStack, Boolean> slotValidator = null;
    private int maxStackSize = 64;
    private int[] outputSlots = null;
    private int[] inputSlots = null;

    public BaseItemStackHandler(int size) {
        this(size, null);
    }

    public BaseItemStackHandler(int size, Function<Integer, Void> onContentsChanged) {
        super(size);
        this.onContentsChanged = onContentsChanged;
        this.slotSizeMap = new HashMap<>();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (this.outputSlots != null && ArrayUtils.contains(this.outputSlots, slot))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.outputSlots != null && !ArrayUtils.contains(this.outputSlots, slot))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotSizeMap.containsKey(slot) ? this.slotSizeMap.get(slot) : this.maxStackSize;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.slotValidator == null || this.slotValidator.apply(slot, stack);
    }

    @Override
    public void onContentsChanged(int slot) {
        if (this.onContentsChanged != null)
            this.onContentsChanged.apply(slot);
    }

    public ItemStack insertItemSuper(int slot, ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItemSuper(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }

    public int[] getInputSlots() {
        return this.inputSlots;
    }

    public int[] getOutputSlots() {
        return this.outputSlots;
    }

    public void setDefaultSlotLimit(int size) {
        this.maxStackSize = size;
    }

    public void addSlotLimit(int slot, int size) {
        this.slotSizeMap.put(slot, size);
    }

    public void setSlotValidator(BiFunction<Integer, ItemStack, Boolean> validator) {
        this.slotValidator = validator;
    }

    public void setInputSlots(int... slots) {
        Arrays.sort(slots);
        this.inputSlots = slots;
    }

    public void setOutputSlots(int... slots) {
        this.outputSlots = slots;
    }

    public boolean isInputEmpty() {
        if (this.inputSlots != null) {
            for (int i : this.inputSlots) {
                if (!this.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isOutputputEmpty() {
        if (this.outputSlots != null) {
            for (int i : this.outputSlots) {
                if (!this.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public IInventory toIInventory() {
        return new Inventory(this.stacks.toArray(new ItemStack[0]));
    }
}
