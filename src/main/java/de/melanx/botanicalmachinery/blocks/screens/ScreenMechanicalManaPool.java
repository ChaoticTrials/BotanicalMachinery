package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMechanicalManaPool> {
    public ScreenMechanicalManaPool(ContainerMechanicalManaPool container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_MANA_POOL_GUI, 81, 37);
        RenderHelper.renderFadedItem(this, TileMechanicalManaPool.CATALYSTS, this.playerInventory.player.ticksExisted, this.relX + 53, this.relY + 47);
    }
}
