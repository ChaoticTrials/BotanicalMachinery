package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMechanicalManaPool> {

    public ScreenMechanicalManaPool(ContainerMechanicalManaPool container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_MANA_POOL_GUI, 81, 37);
    }
}
