package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
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
import java.util.List;

public class ScreenMechanicalDaisy extends ContainerScreen<ContainerMechanicalDaisy> {

    public ScreenMechanicalDaisy(ContainerMechanicalDaisy container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_DAISY_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawSlot(@Nonnull Slot slot) {
        if (slot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot) {
            FluidStack stack = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getFluidInTank(slot.slotNumber);
            if (!stack.isEmpty() && stack.getFluid() != null) {
                int maxAmount = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getTankCapacity(slot.slotNumber);
                int yHeight = Math.round(stack.getAmount() / (float) maxAmount) * 16;
                int yPos = slot.yPos + 16 - yHeight;

                ResourceLocation still = stack.getFluid().getAttributes().getStillTexture(stack);
                TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(still);
                //noinspection ConstantConditions
                minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
                int fluidColor = stack.getFluid().getAttributes().getColor(stack);
                float fluidColorA = ((fluidColor >> 24) & 0xFF) / 255f;
                float fluidColorR = ((fluidColor >> 16) & 0xFF) / 255f;
                float fluidColorG = ((fluidColor >> 8) & 0xFF) / 255f;
                float fluidColorB = ((fluidColor) & 0xFF) / 255f;
                //noinspection deprecation
                GlStateManager.color4f(fluidColorR, fluidColorG, fluidColorB, fluidColorA);
                blit(slot.xPos, yPos, 0, 16, yHeight, sprite);
                //noinspection deprecation
                GlStateManager.color4f(1, 1, 1, 1);
            }
        }
        super.drawSlot(slot);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        //noinspection ConstantConditions
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
            if (this.hoveredSlot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot
                    && ((ContainerMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getStackInSlot(this.hoveredSlot.slotNumber).isEmpty()
                    && !((ContainerMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getFluidInTank(this.hoveredSlot.slotNumber).isEmpty()) {

                FluidStack stack = ((ContainerMechanicalDaisy.ItemAndFluidSlot) this.hoveredSlot).inventory.getFluidInTank(this.hoveredSlot.slotNumber);
                List<String> list = ImmutableList.of(
                        new TranslationTextComponent(stack.getTranslationKey()).getFormattedText(),
                        new StringTextComponent(stack.getAmount() + " / 1000").getFormattedText()
                );

                this.renderTooltip(list, mouseX, mouseY);
                return;
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }
}
