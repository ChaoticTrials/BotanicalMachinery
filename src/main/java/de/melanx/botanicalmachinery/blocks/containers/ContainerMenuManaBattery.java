package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMenuManaBattery extends BlockEntityMenu<BlockEntityManaBattery> {

    public ContainerMenuManaBattery(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 2, 2);

        IItemHandlerModifiable inventory = this.blockEntity.getInventory();
        this.addSlot(new SlotItemHandler(inventory, 0, 53, 25));
        this.addSlot(new SlotItemHandler(inventory, 1, 107, 25));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
