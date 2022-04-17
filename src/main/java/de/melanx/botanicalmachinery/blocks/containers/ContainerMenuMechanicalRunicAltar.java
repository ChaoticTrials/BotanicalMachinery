package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalRunicAltar;
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

public class ContainerMenuMechanicalRunicAltar extends BlockEntityMenu<BlockEntityMechanicalRunicAltar> {

    public ContainerMenuMechanicalRunicAltar(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 17, 33);

        IItemHandlerModifiable inventory = ((BotanicalTile) this.blockEntity).getInventory();
        this.addSlot(new SlotItemHandler(inventory, 0, 90, 43));
        int index = this.addSlotBox(inventory, 1, 8, 26, 4, 18, 4, 18);
        this.addSlotBox(inventory, index, 118, 26, 4, 18, 4, 18, UnrestrictedOutputSlot::new);
        this.layoutPlayerInventorySlots(28, 113);
    }
}
