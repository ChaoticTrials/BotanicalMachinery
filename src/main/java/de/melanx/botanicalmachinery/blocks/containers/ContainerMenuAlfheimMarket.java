package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityAlfheimMarket;
import de.melanx.botanicalmachinery.helper.UnrestrictedOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.moddingx.libx.menu.BlockEntityMenu;

public class ContainerMenuAlfheimMarket extends BlockEntityMenu<BlockEntityAlfheimMarket> {

    public ContainerMenuAlfheimMarket(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 4, 5);

        IItemHandlerModifiable inventory = this.blockEntity.getInventory();
        int index = this.addSlotBox(inventory, 0, 26, 26, 2, 18, 2, 18);
        this.addSlot(new UnrestrictedOutputSlot(inventory, index, 125, 35));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
