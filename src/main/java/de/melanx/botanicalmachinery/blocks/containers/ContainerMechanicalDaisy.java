package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerMechanicalDaisy extends ContainerBase<TileMechanicalDaisy> {

    private final TileMechanicalDaisy.InventoryHandler inventory;

    public ContainerMechanicalDaisy(ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 8, 8);

        this.inventory = this.tile.getInventory();
        this.addSlot(new ItemAndFluidSlot(this.inventory, 0, 79, 16));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 1, 100, 16));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 2, 121, 16));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 3, 79, 37));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 4, 121, 37));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 5, 79, 58));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 6, 100, 58));
        this.addSlot(new ItemAndFluidSlot(this.inventory, 7, 121, 58));

        this.layoutPlayerInventorySlots(8, 84);
    }

    @Nonnull
    @Override
    public ItemStack slotClick(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull PlayerEntity player) {
        if (clickType == ClickType.PICKUP && slot < 8 && !player.inventory.getItemStack().isEmpty() && player.inventory.getItemStack().getCount() == 1) {
            ItemStack inMouse = player.inventory.getItemStack();

            //noinspection ConstantConditions
            @Nullable
            IFluidHandlerItem fluidCap = inMouse.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);

            //noinspection ConstantConditions
            if (inMouse.getItem() instanceof BlockItem || fluidCap == null || fluidCap.getTanks() != 1) {
                return super.slotClick(slot, dragType, clickType, player);
            }

            if (fluidCap.getFluidInTank(0).isEmpty()) {
                // Pick up the fluid
                if (!this.inventory.getFluidInTank(slot).isEmpty()) {
                    int transferred = fluidCap.fill(this.inventory.getFluidInTank(slot).copy(), IFluidHandler.FluidAction.EXECUTE);
                    this.inventory.getFluidInTank(slot).shrink(transferred);
                    if (this.inventory.getFluidInTank(slot).getAmount() <= 0) {
                        this.inventory.setStackInSlot(slot, FluidStack.EMPTY);
                    }
                }
            } else {
                // Fill the fluid
                if (this.inventory.getFluidInTank(slot).isEmpty() && this.inventory.getStackInSlot(slot).isEmpty()) {
                    FluidStack maxDrain = fluidCap.drain(this.inventory.getTankCapacity(slot), IFluidHandler.FluidAction.SIMULATE);
                    if (this.inventory.isFluidValid(slot, maxDrain)) {
                        fluidCap.drain(maxDrain, IFluidHandler.FluidAction.EXECUTE);
                        this.inventory.setStackInSlot(slot, maxDrain);
                    }
                }
            }
            if (!player.isCreative())
                inMouse = fluidCap.getContainer().copy();
            this.detectAndSendChanges();
            player.inventory.setItemStack(inMouse);
            return inMouse;
        }
        return super.slotClick(slot, dragType, clickType, player);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            final int inventorySize = 5;
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;

            if (index < 8) {
                if (!this.mergeItemStack(stack, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, itemstack);
            } else {
                //noinspection ConstantConditions
                IFluidHandlerItem fluidCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                //noinspection ConstantConditions
                if (fluidCap != null && fluidCap.getTanks() == 1) {
                    this.getSlot(index).putStack(this.tryToDepositFluid(fluidCap));
                    return ItemStack.EMPTY;
                } else if (!this.mergeItemStack(stack, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return itemstack;
    }

    // We need to override this to prevent shift-clicking items in slots filled with fluids.
    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack) && slot.isItemValid(stack) && (i >= 8 || this.inventory.getFluidInTank(i).isEmpty())) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack) && (i >= 8 || this.inventory.getFluidInTank(i).isEmpty())) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    private ItemStack tryToDepositFluid(IFluidHandlerItem fluidCap) {
        for (int i = 0; i < 8; i++) {
            if (this.inventory.getStackInSlot(i).isEmpty() && this.inventory.getFluidInTank(i).isEmpty()) {
                FluidStack maxDrain = fluidCap.drain(this.inventory.getTankCapacity(i), IFluidHandler.FluidAction.SIMULATE);
                if (this.inventory.isFluidValid(i, maxDrain)) {
                    fluidCap.drain(maxDrain, IFluidHandler.FluidAction.EXECUTE);
                    this.inventory.setStackInSlot(i, maxDrain);
                }
            }
        }
        return fluidCap.getContainer();
    }

    public static class ItemAndFluidSlot extends SlotItemHandler {

        public final TileMechanicalDaisy.InventoryHandler inventory;

        public ItemAndFluidSlot(TileMechanicalDaisy.InventoryHandler inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.inventory = inventory;
        }
    }
}
