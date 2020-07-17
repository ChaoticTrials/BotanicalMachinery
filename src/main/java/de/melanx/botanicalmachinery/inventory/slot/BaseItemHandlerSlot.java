package de.melanx.botanicalmachinery.inventory.slot;

import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/*
 * Thanks to Cucumber by BlakeBr0
 * https://github.com/BlakeBr0/Cucumber/blob/1.15/src/main/java/com/blakebr0/cucumber/inventory/slot/BaseItemStackHandlerSlot.java
 */
public class BaseItemHandlerSlot extends SlotItemHandler {
    private final BaseItemStackHandler inventory;
    private final int index;

    public BaseItemHandlerSlot(BaseItemStackHandler inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.inventory = inventory;
        this.index = index;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return !this.inventory.extractItemSuper(this.index, 1, true).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return this.inventory.extractItemSuper(this.index, amount, false);
    }
}
