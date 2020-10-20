package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIndustrialAgglomerationFactory extends ContainerBase<TileIndustrialAgglomerationFactory> {

    public ContainerIndustrialAgglomerationFactory(ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 3, 4);

        IItemHandlerModifiable inventory = this.tile.getInventory().getUnrestricted();
        this.addSlot(new SlotItemHandler(inventory, 0, 61, 83));
        this.addSlot(new SlotItemHandler(inventory, 1, 80, 83));
        this.addSlot(new SlotItemHandler(inventory, 2, 99, 83));
        this.addSlot(new SlotOutputOnly(inventory, 3, 80, 25));
        this.layoutPlayerInventorySlots(8, 113);
    }
}
