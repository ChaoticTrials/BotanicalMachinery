package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.slot.BaseItemHandlerSlot;
import de.melanx.botanicalmachinery.inventory.slot.SlotCatalyst;
import de.melanx.botanicalmachinery.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ContainerMechanicalManaPool extends Container {

    public final TileEntity tile;
    private final PlayerEntity player;
    private final IItemHandler playerInventory;
    private final BlockPos pos;
    private final World world;

    public ContainerMechanicalManaPool(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_MECHANICAL_MANA_POOL.get(), windowId);
        this.tile = world.getTileEntity(pos);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.pos = pos;
        this.world = world;

        if (tile instanceof TileBase) {
            BaseItemStackHandler inventory = ((TileBase) tile).getInventory();
            this.addSlot(new SlotCatalyst(inventory, 0, 53, 47));
            this.addSlot(new BaseItemHandlerSlot(inventory, 1, 53, 25));
            this.addSlot(new SlotOutputOnly(inventory, 2, 111, 37));
        }
        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, Registration.BLOCK_MECHANICAL_MANA_POOL.get());
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

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public World getWorld() {
        return this.world;
    }
}
