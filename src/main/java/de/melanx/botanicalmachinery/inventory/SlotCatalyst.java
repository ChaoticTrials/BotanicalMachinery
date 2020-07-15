package de.melanx.botanicalmachinery.inventory;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotCatalyst extends SlotItemHandler {

    public SlotCatalyst(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
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
