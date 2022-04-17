package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.helper.UnrestrictedOutputSlot;
import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import io.github.noeppi_noeppi.libx.menu.slot.OutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMenuIndustrialAgglomerationFactory extends BlockEntityMenu<BlockEntityIndustrialAgglomerationFactory> {

    public ContainerMenuIndustrialAgglomerationFactory(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 3, 4);

        IItemHandlerModifiable inventory = this.blockEntity.getInventory();
        this.addSlot(new SlotItemHandler(inventory, 0, 61, 83));
        this.addSlot(new SlotItemHandler(inventory, 1, 80, 83));
        this.addSlot(new SlotItemHandler(inventory, 2, 99, 83));
        this.addSlot(new UnrestrictedOutputSlot(inventory, 3, 80, 25));
        this.layoutPlayerInventorySlots(8, 113);
    }
}
