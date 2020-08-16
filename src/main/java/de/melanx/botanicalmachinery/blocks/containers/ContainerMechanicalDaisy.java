package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerMechanicalDaisy extends ContainerBase {

    private final TileMechanicalDaisy.InventoryHandler inventory;

    public ContainerMechanicalDaisy(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_MECHANICAL_DAISY.get(), windowId, world, pos, playerInventory, player);

        this.inventory = ((TileMechanicalDaisy) this.tile).getInventory();
        this.addSlot(new ItemAndFluidSlot(inventory, 0, 59, 12));
        this.addSlot(new ItemAndFluidSlot(inventory, 1, 80, 12));
        this.addSlot(new ItemAndFluidSlot(inventory, 2, 101, 12));
        this.addSlot(new ItemAndFluidSlot(inventory, 3, 59, 33));
        this.addSlot(new ItemAndFluidSlot(inventory, 4, 101, 33));
        this.addSlot(new ItemAndFluidSlot(inventory, 5, 59, 54));
        this.addSlot(new ItemAndFluidSlot(inventory, 6, 80, 54));
        this.addSlot(new ItemAndFluidSlot(inventory, 7, 101, 54));

        this.layoutPlayerInventorySlots(8, 84);
    }

    public static ContainerType<ContainerMechanicalDaisy> createContainerType() {
        return IForgeContainerType.create((windowId1, inv, data) -> {
            BlockPos pos1 = data.readBlockPos();
            World world1 = inv.player.getEntityWorld();
            return new ContainerMechanicalDaisy(windowId1, world1, pos1, inv, inv.player);
        });
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
                if (!inventory.getFluidInTank(slot).isEmpty()) {
                    int transferred = fluidCap.fill(inventory.getFluidInTank(slot).copy(), IFluidHandler.FluidAction.EXECUTE);
                    inventory.getFluidInTank(slot).shrink(transferred);
                    if (inventory.getFluidInTank(slot).getAmount() <= 0) {
                        inventory.setStackInSlot(slot, FluidStack.EMPTY);
                    }
                }
            } else {
                // Fill the fluid
                if (inventory.getFluidInTank(slot).isEmpty() && inventory.getStackInSlot(slot).isEmpty()) {
                    FluidStack maxDrain = fluidCap.drain(inventory.getTankCapacity(slot), IFluidHandler.FluidAction.SIMULATE);
                    if (inventory.isFluidValid(slot, maxDrain)) {
                        fluidCap.drain(maxDrain, IFluidHandler.FluidAction.EXECUTE);
                        inventory.setStackInSlot(slot, maxDrain);
                    }
                }
            }
            if (!player.isCreative())
                inMouse = fluidCap.getContainer().copy();
            detectAndSendChanges();
            player.inventory.setItemStack(inMouse);
            return inMouse;
        }
        return super.slotClick(slot, dragType, clickType, player);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
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
                    getSlot(index).putStack(tryToDepositFluid(fluidCap));
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
            slot.onTake(playerIn, stack);
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
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack) && slot.isItemValid(stack) && (i >= 8 || inventory.getFluidInTank(i).isEmpty())) {
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

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack) && (i >= 8 || inventory.getFluidInTank(i).isEmpty())) {
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
            if (inventory.getStackInSlot(i).isEmpty() && inventory.getFluidInTank(i).isEmpty()) {
                FluidStack maxDrain = fluidCap.drain(inventory.getTankCapacity(i), IFluidHandler.FluidAction.SIMULATE);
                if (inventory.isFluidValid(i, maxDrain)) {
                    fluidCap.drain(maxDrain, IFluidHandler.FluidAction.EXECUTE);
                    inventory.setStackInSlot(i, maxDrain);
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
