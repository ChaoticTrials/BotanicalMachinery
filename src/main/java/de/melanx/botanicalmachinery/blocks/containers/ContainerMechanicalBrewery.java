package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.slot.BaseItemHandlerSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ContainerMechanicalBrewery extends ContainerBase {
    public ContainerMechanicalBrewery(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_MECHANICAL_BREWERY.get(), windowId, world, pos, playerInventory, player);

        if (this.tile instanceof TileBase) {
            BaseItemStackHandler inventory = ((TileBase) tile).getInventory();
            this.addSlot(new BaseItemHandlerSlot(inventory, 0, 44, 46));
            this.addSlot(new BaseItemHandlerSlot(inventory, 1, 29, 16));
            this.addSlot(new BaseItemHandlerSlot(inventory, 2, 59, 16));
            this.addSlot(new BaseItemHandlerSlot(inventory, 3, 14, 46));
            this.addSlot(new BaseItemHandlerSlot(inventory, 4, 74, 46));
            this.addSlot(new BaseItemHandlerSlot(inventory, 5, 29, 76));
            this.addSlot(new BaseItemHandlerSlot(inventory, 6, 59, 76));
            this.addSlot(new BaseItemHandlerSlot(inventory, 7, 128, 47));
        }
        this.layoutPlayerInventorySlots(8, 110);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            final int inventorySize = 3;
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;

            if (index == 2) {
                if (!this.mergeItemStack(stack, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, itemstack);
            } else if (index != 1 && index != 0) {
                if (!this.mergeItemStack(stack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                } else if (index < playerInventoryEnd) {
                    if (!this.mergeItemStack(stack, playerInventoryEnd, playerHotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < playerHotbarEnd && !this.mergeItemStack(stack, inventorySize, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, inventorySize, playerHotbarEnd, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stack);
        }
        return itemstack;
    }

    public static <X extends Container> ContainerType<X> createContainerType() {
        ContainerType<Container> containerType = IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            World world = inv.player.getEntityWorld();
            return new ContainerMechanicalBrewery(windowId, world, pos, inv, inv.player);
        });
        return (ContainerType<X>) containerType;
    }
}
