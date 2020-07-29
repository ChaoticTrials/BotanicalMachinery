package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenMechanicalRunicAltar extends ScreenBase<ContainerMechanicalRunicAltar> {
    public ScreenMechanicalRunicAltar(ContainerMechanicalRunicAltar container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.xSize = 216;
        this.ySize = 195;
        this.manaBar.x += 40;
        this.manaBar.y += 20;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_RUNIC_ALTAR_GUI, 81, 37);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        TileMechanicalRunicAltar tile = (TileMechanicalRunicAltar) this.container.tile;
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / 100.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_RUNIC_ALTAR_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(relX + 87, relY + 64, 216, 0, (int) (22 * pct), 16);
        }
    }
}
