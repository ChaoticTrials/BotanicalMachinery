package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.util.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMechanicalManaPool extends ContainerBase<TileMechanicalManaPool> {
    public ContainerMechanicalManaPool(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_MECHANICAL_MANA_POOL.get(), windowId, world, pos, playerInventory, player, 2, 3);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        // We pass in the catalyst slot at first because it'll be scanned first in transferStackInSlot
        this.addSlot(new SlotItemHandler(inventory, 1, 53, 25));
        this.addSlot(new SlotItemHandler(inventory, 0, 53, 47));
        this.addSlot(new SlotOutputOnly(inventory, 2, 111, 37));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
