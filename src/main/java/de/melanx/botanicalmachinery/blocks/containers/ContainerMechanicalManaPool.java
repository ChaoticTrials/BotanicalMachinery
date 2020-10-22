package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMechanicalManaPool extends ContainerBase<TileMechanicalManaPool> {

    public ContainerMechanicalManaPool(ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 2, 3);

        IItemHandlerModifiable inventory = ((BotanicalTile) this.tile).getInventory().getUnrestricted();
        // We pass in the catalyst slot at first because it'll be scanned first in transferStackInSlot
        this.addSlot(new SlotItemHandler(inventory, 1, 53, 25));
        this.addSlot(new SlotItemHandler(inventory, 0, 53, 47));
        this.addSlot(new SlotOutputOnly(inventory, 2, 111, 37));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
