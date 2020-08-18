package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.util.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ContainerAlfheimMarket extends ContainerBase<TileAlfheimMarket> {
    public ContainerAlfheimMarket(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_ALFHEIM_MARKET.get(), windowId, world, pos, playerInventory, player, 4, 5);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        int index = this.addSlotBox(inventory, 0, 26, 26, 2, 18, 2, 18);
        this.addSlot(new SlotOutputOnly(inventory, index, 125, 35));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
