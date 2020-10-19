package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.core.registration.Registration;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIndustrialAgglomerationFactory extends ContainerBase<TileIndustrialAgglomerationFactory> {
    public ContainerIndustrialAgglomerationFactory(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), windowId, world, pos, playerInventory, player, 3, 4);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        this.addSlot(new SlotItemHandler(inventory, 0, 61, 83));
        this.addSlot(new SlotItemHandler(inventory, 1, 80, 83));
        this.addSlot(new SlotItemHandler(inventory, 2, 99, 83));
        this.addSlot(new SlotOutputOnly(inventory, 3, 80, 25));
        this.layoutPlayerInventorySlots(8, 113);
    }
}
