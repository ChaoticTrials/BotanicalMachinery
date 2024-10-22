package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalBrewery;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.GhostItemRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ScreenMechanicalBrewery extends ScreenBase<ContainerMenuMechanicalBrewery> {

    public ScreenMechanicalBrewery(ContainerMenuMechanicalBrewery menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 192;
        this.manaBar.y = 28;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(guiGraphics, LibResources.MECHANICAL_BREWERY_GUI);

        BlockEntityMechanicalBrewery blockEntity = this.menu.getBlockEntity();
        if (blockEntity.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            GhostItemRenderer.renderGhostItem(BlockEntityMechanicalBrewery.BREW_CONTAINER.stream().map(ItemStack::new).toList(), guiGraphics, this.relX + 44, this.relY + 48);
        }

        if (blockEntity.getProgress() > 0) {
            float pct = Math.min(blockEntity.getProgress() / (float) blockEntity.getMaxProgress(), 1.0F);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(guiGraphics, LibResources.MECHANICAL_BREWERY_GUI, this.relX + 96, this.relY + 48, 176, 0, Math.round(22 * pct), 16);
        }
    }
}
