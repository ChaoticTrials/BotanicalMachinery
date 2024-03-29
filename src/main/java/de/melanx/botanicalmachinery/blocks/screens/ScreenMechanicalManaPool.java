package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.GhostItemRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMenuMechanicalManaPool> {

    public ScreenMechanicalManaPool(ContainerMenuMechanicalManaPool menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(guiGraphics, LibResources.MECHANICAL_MANA_POOL_GUI);

        BlockEntityMechanicalManaPool blockEntity = this.menu.getBlockEntity();
        if (blockEntity.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            GhostItemRenderer.renderGhostItem(blockEntity.getCatalysts().stream().map(ItemStack::new).toList(), guiGraphics, this.relX + 53, this.relY + 47);
        }
    }
}
