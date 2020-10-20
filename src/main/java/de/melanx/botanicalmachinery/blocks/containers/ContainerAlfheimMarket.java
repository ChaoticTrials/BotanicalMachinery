package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ContainerAlfheimMarket extends ContainerBase<TileAlfheimMarket> {

    public ContainerAlfheimMarket(ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 4, 5);

        IItemHandlerModifiable inventory = this.tile.getInventory().getUnrestricted();
        int index = this.addSlotBox(inventory, 0, 26, 26, 2, 18, 2, 18);
        this.addSlot(new SlotOutputOnly(inventory, index, 125, 35));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
