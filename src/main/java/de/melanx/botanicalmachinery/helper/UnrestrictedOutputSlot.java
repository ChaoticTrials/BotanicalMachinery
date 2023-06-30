package de.melanx.botanicalmachinery.helper;

import net.minecraftforge.items.IItemHandler;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.menu.slot.OutputSlot;

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
