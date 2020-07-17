package de.melanx.botanicalmachinery.inventory.slot;

import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotOutputOnly extends BaseItemHandlerSlot {
    public SlotOutputOnly(BaseItemStackHandler inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }
}
