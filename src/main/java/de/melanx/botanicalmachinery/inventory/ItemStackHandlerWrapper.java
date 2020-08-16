package de.melanx.botanicalmachinery.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
 * Thanks to Cucumber by BlakeBr0
 * https://github.com/BlakeBr0/Cucumber/blob/1.15/src/main/java/com/blakebr0/cucumber/inventory/SidedItemStackHandlerWrapper.java
 */
public class ItemStackHandlerWrapper implements IItemHandlerModifiable {
    private final IItemHandlerModifiable inventory;
    private final BiFunction<Integer, ItemStack, Boolean> canInsert;
    private final Function<Integer, Boolean> canExtract;

    public ItemStackHandlerWrapper(IItemHandlerModifiable inventory, @Nullable Function<Integer, Boolean> canExtract, @Nullable BiFunction<Integer, ItemStack, Boolean> canInsert) {
        this.inventory = inventory;
        this.canExtract = canExtract;
        this.canInsert = canInsert;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.inventory.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!this.isItemValid(slot, stack)) return stack;
        return this.inventory.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.canExtract != null && !this.canExtract.apply(slot))
            return ItemStack.EMPTY;

        return this.inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.canInsert == null || this.canInsert.apply(slot, stack);
    }

    public static LazyOptional<IItemHandlerModifiable> create(IItemHandlerModifiable inv) {
        return LazyOptional.of(() -> new ItemStackHandlerWrapper(inv, null, null));
    }

    public static LazyOptional<IItemHandlerModifiable> create(IItemHandlerModifiable inv, @Nullable Function<Integer, Boolean> canExtract, @Nullable BiFunction<Integer, ItemStack, Boolean> canInsert) {
        return LazyOptional.of(() -> new ItemStackHandlerWrapper(inv, canExtract, canInsert));
    }
}
