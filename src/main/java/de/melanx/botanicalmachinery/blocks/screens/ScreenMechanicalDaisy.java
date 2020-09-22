package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalDaisy;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class ScreenMechanicalDaisy extends ContainerScreen<ContainerMechanicalDaisy> {

    private static final ResourceLocation PURE_DAISY_TEXTURE = new ResourceLocation("botania", "block/pure_daisy");

    public ScreenMechanicalDaisy(ContainerMechanicalDaisy container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(ms);
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_DAISY_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(ms, relX, relY, 0, 0, this.xSize, this.ySize);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack ms, int mouseX, int mouseY) {
        String s = this.title.getString();
        this.font.drawString(ms, s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, Color.DARK_GRAY.getRGB());
        this.font.drawString(ms, this.playerInventory.getDisplayName().getString(), 8.0F, (float) (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());

        GlStateManager.pushMatrix();
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableBlend();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(PURE_DAISY_TEXTURE);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        blit(ms, 12, 16, 0, 48, 48, sprite);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        this.renderHoveredTooltip(ms, mouseX - this.guiLeft, mouseY - this.guiTop);
    }

    @Override
    public void moveItems(@Nonnull MatrixStack ms, @Nonnull Slot slot) {
        if (slot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot) {
            FluidStack stack = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getFluidInTank(slot.slotNumber);
            if (!stack.isEmpty() && stack.getFluid() != null) {
                int maxAmount = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getTankCapacity(slot.slotNumber);
                int yHeight = Math.round(stack.getAmount() / (float) maxAmount) * 16;
                int yPos = slot.yPos + 16 - yHeight;

                ResourceLocation still = stack.getFluid().getAttributes().getStillTexture(stack);
                TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(still);
                //noinspection ConstantConditions
                this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
                int fluidColor = stack.getFluid().getAttributes().getColor(stack);
                float fluidColorA = ((fluidColor >> 24) & 0xFF) / 255f;
                float fluidColorR = ((fluidColor >> 16) & 0xFF) / 255f;
                float fluidColorG = ((fluidColor >> 8) & 0xFF) / 255f;
                float fluidColorB = ((fluidColor) & 0xFF) / 255f;
                //noinspection deprecation
                GlStateManager.color4f(fluidColorR, fluidColorG, fluidColorB, fluidColorA);
                blit(ms, slot.xPos, yPos, 0, 16, yHeight, sprite);
                //noinspection deprecation
                GlStateManager.color4f(1, 1, 1, 1);
            }
        }
        super.moveItems(ms, slot);
    }

    @Override
    protected void renderHoveredTooltip(@Nonnull MatrixStack ms, int mouseX, int mouseY) {
        //noinspection ConstantConditions
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
            if (this.hoveredSlot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot
                    && ((ContainerMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getStackInSlot(this.hoveredSlot.slotNumber).isEmpty()
                    && !((ContainerMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getFluidInTank(this.hoveredSlot.slotNumber).isEmpty()) {

                FluidStack stack = ((ContainerMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getFluidInTank(this.hoveredSlot.slotNumber);
                List<ITextComponent> list = ImmutableList.of(
                        new TranslationTextComponent(stack.getTranslationKey()),
                        new StringTextComponent(stack.getAmount() + " / 1000")
                );

                this.func_243308_b(ms, list, mouseX, mouseY);
                return;
            }
        }
        super.renderHoveredTooltip(ms, mouseX, mouseY);
    }
}
