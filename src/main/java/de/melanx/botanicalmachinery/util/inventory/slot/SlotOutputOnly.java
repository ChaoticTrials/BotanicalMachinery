package de.melanx.botanicalmachinery.util.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * A slot that only accepts items in an ingredient. Pass Ingredient.EMPTY for an output only slot.
 */
public class SlotOutputOnly extends SlotItemHandler {

    public SlotOutputOnly(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }
}
