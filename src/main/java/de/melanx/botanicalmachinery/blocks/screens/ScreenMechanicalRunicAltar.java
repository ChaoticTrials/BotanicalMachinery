package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalRunicAltar;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.GhostItemRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.BotaniaBlocks;

import javax.annotation.Nonnull;

public class ScreenMechanicalRunicAltar extends ScreenBase<ContainerMenuMechanicalRunicAltar> {

    public ScreenMechanicalRunicAltar(ContainerMenuMechanicalRunicAltar menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 216;
        this.imageHeight = 195;
        this.manaBar.x += 40;
        this.manaBar.y += 20;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(guiGraphics, LibResources.MECHANICAL_RUNIC_ALTAR_GUI);

        BlockEntityMechanicalRunicAltar blockEntity = this.menu.getBlockEntity();
        if (blockEntity.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            GhostItemRenderer.renderGhostItem(new ItemStack(BotaniaBlocks.livingrock), guiGraphics, this.relX + 90, this.relY + 43);
        }

        if (blockEntity.getProgress() > 0) {
            float pct = Math.min(blockEntity.getProgress() / (float) blockEntity.getMaxProgress(), 1.0F);
            RenderHelper.drawTexturedModalRect(guiGraphics, LibResources.MECHANICAL_RUNIC_ALTAR_GUI, this.relX + 87, this.relY + 64, this.imageWidth, 0, Math.round(22 * pct), 16);
        }
    }
}
