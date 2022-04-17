package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ScreenIndustrialAgglomerationFactory extends ScreenBase<ContainerMenuIndustrialAgglomerationFactory> {

    public ScreenIndustrialAgglomerationFactory(ContainerMenuIndustrialAgglomerationFactory menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 195;
        this.manaBar.x -= 5;
        this.manaBar.y += 23;
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(poseStack, LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI);
        BlockEntityIndustrialAgglomerationFactory blockEntity = this.menu.getBlockEntity();

        if (blockEntity.getProgress() > 0) {
            float pct = Math.min(blockEntity.getProgress() / (float) blockEntity.getMaxProgress(), 1.0F);
            RenderSystem.setShaderTexture(0, LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI);
            int height = Math.round(25 * pct);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(poseStack, this.relX + 73, this.relY + 76 - height, 176, 25 - height, 30, height);
        }
    }
}
