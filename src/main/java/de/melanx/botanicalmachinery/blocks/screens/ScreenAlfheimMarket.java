package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityAlfheimMarket;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ScreenAlfheimMarket extends ScreenBase<ContainerMenuAlfheimMarket> {

    public ScreenAlfheimMarket(ContainerMenuAlfheimMarket menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(guiGraphics, LibResources.ALFHEIM_MARKET_GUI);
        BlockEntityAlfheimMarket blockEntity = this.menu.getBlockEntity();

        if (blockEntity.getProgress() > 0) {
            float pct = Math.min(blockEntity.getProgress() / (float) blockEntity.getMaxProgress(), 1.0F);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(guiGraphics, LibResources.ALFHEIM_MARKET_GUI, this.relX + 77, this.relY + 35, 176, 0, Math.round(22 * pct), 16);
        }
    }
}
