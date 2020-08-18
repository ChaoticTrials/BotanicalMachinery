package de.melanx.botanicalmachinery.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/*
 * Thanks to Cucumber by BlakeBr0
 * https://github.com/BlakeBr0/Cucumber/blob/1.15/src/main/java/com/blakebr0/cucumber/inventory/BaseItemStackHandler.java
 */
public class BaseItemStackHandler extends ItemStackHandler {

    // An IItemHandlerModifiable that bypasses every slot validity check when trying to extract and insert.
    private final IItemHandlerModifiable unrestricted = new Unrestricted();
    private final Consumer<Integer> onContentsChanged;
    private final Map<Integer, Integer> slotSizeMap;
    private BiFunction<Integer, ItemStack, Boolean> slotValidator;
    private int maxStackSize = 64;
    private int[] outputSlots = null;
    private int[] inputSlots = null;

    public BaseItemStackHandler(int size) {
        this(size, null);
    }

    public BaseItemStackHandler(int size, Consumer<Integer> onContentsChanged) {
        super(size);
        this.onContentsChanged = onContentsChanged;
        this.slotSizeMap = new HashMap<>();
        this.slotValidator = null;
    }

    public BaseItemStackHandler(int size, Consumer<Integer> onContentsChanged, BiFunction<Integer, ItemStack, Boolean> slotValidator) {
        super(size);
        this.onContentsChanged = onContentsChanged;
        this.slotSizeMap = new HashMap<>();
        this.slotValidator = slotValidator;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.outputSlots != null && ArrayUtils.contains(this.outputSlots, slot))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
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
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.slotValidator == null || this.slotValidator.apply(slot, stack);
    }

    @Override
    public void onContentsChanged(int slot) {
        if (this.onContentsChanged != null)
            this.onContentsChanged.accept(slot);
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

    public boolean isOutputEmpty() {
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

    public IItemHandlerModifiable getUnrestricted() {
        return this.unrestricted;
    }

    private class Unrestricted implements IItemHandlerModifiable {

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            BaseItemStackHandler.this.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return BaseItemStackHandler.this.getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return BaseItemStackHandler.this.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            BaseItemStackHandler.this.validateSlotIndex(slot);
            ItemStack existing = this.getStackInSlot(slot);
            int limit = BaseItemStackHandler.this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }
                limit -= existing.getCount();
            }
            if (limit <= 0)
                return stack;
            boolean reachedLimit = stack.getCount() > limit;
            if (!simulate) {
                if (existing.isEmpty()) {
                    this.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
                BaseItemStackHandler.this.onContentsChanged(slot);
            }
            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0)
                return ItemStack.EMPTY;
            BaseItemStackHandler.this.validateSlotIndex(slot);
            ItemStack existing = this.getStackInSlot(slot);
            if (existing.isEmpty())
                return ItemStack.EMPTY;
            int toExtract = Math.min(amount, existing.getMaxStackSize());
            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    this.setStackInSlot(slot, ItemStack.EMPTY);
                    BaseItemStackHandler.this.onContentsChanged(slot);
                    return existing;
                } else {
                    return existing.copy();
                }
            } else {
                if (!simulate) {
                    this.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    BaseItemStackHandler.this.onContentsChanged(slot);
                }
                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return BaseItemStackHandler.this.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return BaseItemStackHandler.this.isItemValid(slot, stack);
        }
    }
}
