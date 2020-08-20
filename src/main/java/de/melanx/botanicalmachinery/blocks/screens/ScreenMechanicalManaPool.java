package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMechanicalManaPool> {

    private static int i;
    private long lastTime;

    public ScreenMechanicalManaPool(ContainerMechanicalManaPool container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_MANA_POOL_GUI, 81, 37);

        if (System.currentTimeMillis() - this.lastTime > 2000) {
            this.lastTime = System.currentTimeMillis();
            i--;
            if (i < 0) i = RecipeHelper.manaPoolCatalysts.size() - 1;
        }
        RenderHelper.renderFadedItem(this, RecipeHelper.manaPoolCatalysts.get(i), this.relX + 53, this.relY + 47);
    }
}
