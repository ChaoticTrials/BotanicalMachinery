package de.melanx.botanicalmachinery.inventory.slot;

import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;

public class SlotSpecialExclude extends BaseItemHandlerSlot {
    private final Item[] forbiddenItems;
    public SlotSpecialExclude(BaseItemStackHandler inventory, int index, int xPosition, int yPosition, Item... items) {
        super(inventory, index, xPosition, yPosition);
        this.forbiddenItems = items;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return !new ArrayList<>(Arrays.asList(forbiddenItems)).contains(stack.getItem());
    }
}
