package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import de.melanx.botanicalmachinery.helper.UnrestrictedOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.moddingx.libx.menu.BlockEntityMenu;

public class ContainerMenuMechanicalManaPool extends BlockEntityMenu<BlockEntityMechanicalManaPool> {

    public ContainerMenuMechanicalManaPool(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 2, 3);

        IItemHandlerModifiable inventory = ((BotanicalTile) this.blockEntity).getInventory();
        this.addSlot(new SlotItemHandler(inventory, 0, 53, 47));
        this.addSlot(new SlotItemHandler(inventory, 1, 53, 25));
        this.addSlot(new UnrestrictedOutputSlot(inventory, 2, 111, 37));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
