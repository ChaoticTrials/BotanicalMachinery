package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerManaBattery extends ContainerBase<TileManaBattery> {

    public ContainerManaBattery(ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 2, 2);

        IItemHandlerModifiable inventory = this.tile.getInventory().getUnrestricted();
        this.addSlot(new SlotItemHandler(inventory, 0, 53, 25));
        this.addSlot(new SlotItemHandler(inventory, 1, 107, 25));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
