package de.melanx.botanicalmachinery.inventory.slot;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotCatalyst extends BaseItemHandlerSlot {
    public SlotCatalyst(BaseItemStackHandler inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return BotanicalMachinery.catalysts.contains(stack.getItem());
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
