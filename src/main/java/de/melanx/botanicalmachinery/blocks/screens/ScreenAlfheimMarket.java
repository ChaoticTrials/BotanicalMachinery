package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenAlfheimMarket extends ScreenBase<ContainerAlfheimMarket> {
    public ScreenAlfheimMarket(ContainerAlfheimMarket container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.ALFHEIM_MARKET_GUI, 81, 37);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        TileAlfheimMarket tile = (TileAlfheimMarket) this.container.tile;
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / (float) TileAlfheimMarket.WORKING_DURATION, 1.0F);
            this.minecraft.getTextureManager().bindTexture(LibResources.ALFHEIM_MARKET_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(relX + 77, relY + 35, 176, 0, Math.round(22 * pct), 16);
        }
    }
}
