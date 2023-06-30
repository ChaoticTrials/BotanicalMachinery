package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalDaisy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;
import org.moddingx.libx.menu.BlockEntityMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerMenuMechanicalDaisy extends BlockEntityMenu<BlockEntityMechanicalDaisy> {

    private final BlockEntityMechanicalDaisy.InventoryHandler inventory;

    public ContainerMenuMechanicalDaisy(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 8, 8);

        this.inventory = this.blockEntity.getInventory();
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

    @Override
    public void clicked(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull Player player) {
        if (clickType == ClickType.PICKUP && slot >= 0 && slot < 8 && !this.getCarried().isEmpty() && this.getCarried().getCount() == 1) {
            ItemStack inMouse = this.getCarried();

            //noinspection ConstantConditions
            @Nullable
            IFluidHandlerItem fluidCap = inMouse.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);

            //noinspection ConstantConditions
            if (inMouse.getItem() instanceof BlockItem || fluidCap == null || fluidCap.getTanks() != 1) {
                super.clicked(slot, dragType, clickType, player);
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
            inMouse = fluidCap.getContainer().copy();

            this.broadcastChanges();
            this.setCarried(inMouse);
        }

        super.clicked(slot, dragType, clickType, player);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            final int inventorySize = 5;
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;

            if (index < 8) {
                if (!this.moveItemStackTo(stack, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, itemstack);
            } else {
                //noinspection ConstantConditions
                IFluidHandlerItem fluidCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
                //noinspection ConstantConditions
                if (fluidCap != null && fluidCap.getTanks() == 1) {
                    this.getSlot(index).set(this.tryToDepositFluid(fluidCap));
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(stack, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
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
    protected boolean moveItemStackTo(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
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

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack) && slot.mayPlace(stack) && (i >= 8 || this.inventory.getFluidInTank(i).isEmpty())) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
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

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(stack) && (i >= 8 || this.inventory.getFluidInTank(i).isEmpty())) {
                    if (stack.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(stack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
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

        public final BlockEntityMechanicalDaisy.InventoryHandler inventory;

        public ItemAndFluidSlot(BlockEntityMechanicalDaisy.InventoryHandler inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.inventory = inventory;
        }
    }
}
