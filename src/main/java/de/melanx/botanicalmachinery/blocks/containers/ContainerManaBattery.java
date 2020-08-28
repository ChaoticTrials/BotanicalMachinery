package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerManaBattery extends ContainerBase<TileManaBattery> {
    public ContainerManaBattery(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.CONTAINER_MANA_BATTERY.get(), windowId, world, pos, playerInventory, player, 2, 2);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        this.addSlot(new SlotItemHandler(inventory, 0, 53, 25));
        this.addSlot(new SlotItemHandler(inventory, 1, 107, 25));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
