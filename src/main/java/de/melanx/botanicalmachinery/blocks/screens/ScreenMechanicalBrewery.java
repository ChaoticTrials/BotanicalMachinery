package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenMechanicalBrewery extends ScreenBase<ContainerMechanicalBrewery> {

    private static int i;
    private long lastTime;

    public ScreenMechanicalBrewery(ContainerMechanicalBrewery container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.ySize = 192;
        this.manaBar.y = 28;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_BREWERY_GUI);

        if (this.container.tile instanceof TileMechanicalBrewery && ((TileMechanicalBrewery) this.container.tile).getInventory().getStackInSlot(0).isEmpty()) {
            if (System.currentTimeMillis() - lastTime > 2000) {
                lastTime = System.currentTimeMillis();
                i--;
                if (i < 0) i = RecipeHelper.brewContainer.size() - 1;
            }
            RenderHelper.renderFadedItem(this, RecipeHelper.brewContainer.get(i), this.relX + 44, this.relY + 46);
        }
    }
}
