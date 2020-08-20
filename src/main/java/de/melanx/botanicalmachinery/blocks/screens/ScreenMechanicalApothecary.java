package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ScreenMechanicalApothecary extends ContainerScreen<ContainerMechanicalApothecary> {
    private final int relX;
    private final int relY;

    public ScreenMechanicalApothecary(ContainerMechanicalApothecary screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 196;
        this.ySize = 195;
        this.relX = (this.width - this.xSize) / 2;
        this.relY = (this.height - this.ySize) / 2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_APOTHECARY_GUI);

        this.blit(this.relX, this.relY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, Color.DARK_GRAY.getRGB());
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());
        this.font.drawString(String.valueOf(((TileMechanicalApothecary) this.container.getWorld().getTileEntity(this.container.getPos())).getFluidInventory().getFluidAmount()), 160.0F, this.ySize - 94, Color.BLUE.getRGB());

        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_APOTHECARY_GUI);
        this.blit(this.relX + 163, this.relY + 15, this.xSize, 0, 17, 81);
    }
}
