package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalDaisy;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.List;

public class ScreenMechanicalDaisy extends AbstractContainerScreen<ContainerMenuMechanicalDaisy> {

    private static final ResourceLocation PURE_DAISY_TEXTURE = new ResourceLocation("botania", "block/pure_daisy");

    public ScreenMechanicalDaisy(ContainerMenuMechanicalDaisy menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(LibResources.MECHANICAL_DAISY_GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String s = this.title.getString();
        guiGraphics.drawString(this.font, s, (float) (this.imageWidth / 2 - this.font.width(s) / 2), 6.0F, Color.DARK_GRAY.getRGB(), false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle.getString(), 8.0F, (float) (this.imageHeight - 96 + 2), Color.DARK_GRAY.getRGB(), false);

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(PURE_DAISY_TEXTURE);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        guiGraphics.blit(12, 16, 0, 48, 48, sprite);
        this.renderTooltip(guiGraphics, mouseX - this.leftPos, mouseY - this.topPos);
    }

    @Override
    public void renderSlot(@Nonnull GuiGraphics guiGraphics, @Nonnull Slot slot) {
        if (slot instanceof ContainerMenuMechanicalDaisy.ItemAndFluidSlot) {
            FluidStack stack = ((ContainerMenuMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getFluidInTank(slot.index);
            if (!stack.isEmpty() && stack.getFluid() != null) {
                int maxAmount = ((ContainerMenuMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getTankCapacity(slot.index);
                int yHeight = Math.round(stack.getAmount() / (float) maxAmount) * 16;
                int yPos = slot.y + 16 - yHeight;

                IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
                ResourceLocation still = fluidTypeExtensions.getStillTexture(stack);
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                int fluidColor = fluidTypeExtensions.getTintColor(stack);
                float fluidColorA = ((fluidColor >> 24) & 0xFF) / 255f;
                float fluidColorR = ((fluidColor >> 16) & 0xFF) / 255f;
                float fluidColorG = ((fluidColor >> 8) & 0xFF) / 255f;
                float fluidColorB = ((fluidColor) & 0xFF) / 255f;
                RenderSystem.setShaderColor(fluidColorR, fluidColorG, fluidColorB, fluidColorA);
                guiGraphics.blit(slot.x, yPos, 0, 16, yHeight, sprite);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        super.renderSlot(guiGraphics, slot);
    }

    @Override
    protected void renderTooltip(@Nonnull GuiGraphics guiGraphics, int x, int y) {
        //noinspection ConstantConditions
        if (this.minecraft.player.containerMenu.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.hoveredSlot instanceof ContainerMenuMechanicalDaisy.ItemAndFluidSlot
                    && ((ContainerMenuMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getStackInSlot(this.hoveredSlot.index).isEmpty()
                    && !((ContainerMenuMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getFluidInTank(this.hoveredSlot.index).isEmpty()) {

                FluidStack stack = ((ContainerMenuMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getFluidInTank(this.hoveredSlot.index);
                List<Component> list = ImmutableList.of(
                        Component.translatable(stack.getTranslationKey()),
                        Component.literal(stack.getAmount() + " / 1000")
                );

                guiGraphics.renderComponentTooltip(this.font, list, x, y);
                return;
            }
        }

        super.renderTooltip(guiGraphics, x, y);
    }
}
