package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.common.item.ModItems;

public class ScreenIndustrialAgglomerationFactory extends ScreenBase<ContainerIndustrialAgglomerationFactory> {
    public ScreenIndustrialAgglomerationFactory(ContainerIndustrialAgglomerationFactory container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.ySize = 195;
        this.manaBar.x -= 5;
        this.manaBar.y += 23;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI, 81, 37);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        RenderHelper.renderFadedItem(this, ModItems.manaSteel, relX + 61, relY + 83);
        RenderHelper.renderFadedItem(this, ModItems.manaDiamond, relX + 80, relY + 83);
        RenderHelper.renderFadedItem(this, ModItems.manaPearl, relX + 99, relY + 83);
        TileIndustrialAgglomerationFactory tile = (TileIndustrialAgglomerationFactory) this.container.tile;
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / 100.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(relX + 73, relY + 76, 176, 25, 30, Math.round(-(25 * pct)));
        }
    }

}
