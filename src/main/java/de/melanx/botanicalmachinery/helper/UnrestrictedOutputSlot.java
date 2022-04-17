package de.melanx.botanicalmachinery.helper;

import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.menu.slot.OutputSlot;
import net.minecraftforge.items.IItemHandler;

public class UnrestrictedOutputSlot extends OutputSlot {

    public UnrestrictedOutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(getHandler(itemHandler), index, xPosition, yPosition);
    }
    
    private static IItemHandler getHandler(IItemHandler handler) {
        if (handler instanceof BaseItemStackHandler b) {
            return b.getUnrestricted();
        } else {
            return handler;
        }
    }
}
