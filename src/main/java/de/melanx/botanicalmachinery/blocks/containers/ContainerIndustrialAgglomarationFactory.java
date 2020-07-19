package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.slot.BaseItemHandlerSlot;
import de.melanx.botanicalmachinery.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ContainerIndustrialAgglomarationFactory extends ContainerBase {
    public ContainerIndustrialAgglomarationFactory(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_INDUSTRIAL_AGGLOMARATION_FACTORY.get(), windowId, world, pos, playerInventory, player);
        if (this.tile instanceof TileBase) {
            BaseItemStackHandler inventory = ((TileBase) tile).getInventory();
            this.addSlot(new BaseItemHandlerSlot(inventory, 0, 61, 83));
            this.addSlot(new BaseItemHandlerSlot(inventory, 1, 80, 83));
            this.addSlot(new BaseItemHandlerSlot(inventory, 2, 99, 83));
            this.addSlot(new BaseItemHandlerSlot(inventory, 3, 80, 25));
        }
        this.layoutPlayerInventorySlots(8, 113);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            final int inventorySize = 4;
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;

            if (index == 3) {
                if (!this.mergeItemStack(stack, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, itemstack);
            } else if (index > 2) {
                if (!this.mergeItemStack(stack, 0, 3, false)) {
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

    public static <T extends Container> ContainerType<T> createContainerType() {
        ContainerType<Container> containerType = IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            World world = inv.player.getEntityWorld();
            return new ContainerIndustrialAgglomarationFactory(windowId, world, pos, inv, inv.player);
        });
        return (ContainerType<T>) containerType;
    }
}
