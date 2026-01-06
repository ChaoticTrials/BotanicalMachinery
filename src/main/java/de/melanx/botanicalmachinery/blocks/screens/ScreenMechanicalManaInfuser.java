package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaInfuser;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaInfuser;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ScreenMechanicalManaInfuser extends ScreenBase<ContainerMenuMechanicalManaInfuser> {

    public ScreenMechanicalManaInfuser(ContainerMenuMechanicalManaInfuser menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 195;
        this.manaBar.x -= 5;
        this.manaBar.y += 23;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(guiGraphics, LibResources.MECHANICAL_MANA_INFUSER_GUI);
        BlockEntityMechanicalManaInfuser blockEntity = this.menu.getBlockEntity();

        if (blockEntity.getProgress() > 0) {
            float pct = Math.min(blockEntity.getProgress() / (float) blockEntity.getMaxProgress(), 1.0F);
            int height = Math.round(25 * pct);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(guiGraphics, LibResources.MECHANICAL_MANA_INFUSER_GUI, this.relX + 73, this.relY + 76 - height, 176, 25 - height, 30, height);
        }
    }
}
