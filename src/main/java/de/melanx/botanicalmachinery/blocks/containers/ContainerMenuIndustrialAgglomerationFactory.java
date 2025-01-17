package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.helper.UnrestrictedOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.moddingx.libx.menu.BlockEntityMenu;

public class ContainerMenuIndustrialAgglomerationFactory extends BlockEntityMenu<BlockEntityIndustrialAgglomerationFactory> {

    public ContainerMenuIndustrialAgglomerationFactory(MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, LibXServerConfig.SlotCount.industrialAgglomerationFactoryInput, LibXServerConfig.SlotCount.industrialAgglomerationFactoryInput + 1);

        IItemHandlerModifiable inventory = this.blockEntity.getInventory();
//        this.addSlot(new SlotItemHandler(inventory, 0, 61, 83));
//        this.addSlot(new SlotItemHandler(inventory, 1, 80, 83));
//        this.addSlot(new SlotItemHandler(inventory, 2, 99, 83));

        int inputSlotCount = 9;
        int startX = 61;
        int startY = 83;
        int slotSpacing = 19;

        for (int i = 0; i < inputSlotCount; i++) {
            int x = startX + (i % 3) * slotSpacing;
            int y = startY + (i / 3) * slotSpacing;
            this.addSlot(new SlotItemHandler(inventory, i, x, y));
        }

        this.addSlot(new UnrestrictedOutputSlot(inventory, LibXServerConfig.SlotCount.industrialAgglomerationFactoryInput, 80, 25));
        this.layoutPlayerInventorySlots(8, 113);
    }
}
