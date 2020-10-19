package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.core.Registration;
import io.github.noeppi_noeppi.libx.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMechanicalRunicAltar extends ContainerBase<TileMechanicalRunicAltar> {
    public ContainerMechanicalRunicAltar(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_MECHANICAL_RUNIC_ALTAR.get(), windowId, world, pos, playerInventory, player, 17, 33);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        this.addSlot(new SlotItemHandler(inventory, 0, 90, 43));
        int index = this.addSlotBox(inventory, 1, 8, 26, 4, 18, 4, 18);
        this.addSlotBox(inventory, index, 118, 26, 4, 18, 4, 18, SlotOutputOnly::new);
        this.layoutPlayerInventorySlots(28, 113);
    }
}
