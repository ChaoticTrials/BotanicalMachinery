package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.awt.*;

public class ScreenMechanicalApothecary extends ContainerScreen<ContainerMechanicalApothecary> {
    private int relX;
    private int relY;
    private TileMechanicalApothecary tile;
    private final static ResourceLocation water = new ResourceLocation("block/water_still");

    public ScreenMechanicalApothecary(ContainerMechanicalApothecary screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 196;
        this.ySize = 195;
        this.relX = (this.width - this.xSize) / 2;
        this.relY = (this.height - this.ySize) / 2;
        this.tile = (TileMechanicalApothecary) this.container.getWorld().getTileEntity(this.container.getPos());
    }

    @Override
    public void init(@Nonnull Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
        super.init(p_init_1_, p_init_2_, p_init_3_);
        this.relX = (p_init_2_ - this.xSize) / 2;
        this.relY = (p_init_3_ - this.ySize) / 2;
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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, Color.DARK_GRAY.getRGB());
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());

        float pct = Math.min((float) this.tile.getFluidInventory().getFluidAmount() / TileMechanicalApothecary.FLUID_CAPACITY, 1.0F);
        this.minecraft.getTextureManager().bindTexture(water);
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(water);
        this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        int fluidColor = Fluids.WATER.getAttributes().getColor();
        float fluidColorA = ((fluidColor >> 24) & 0xFF) / 255f;
        float fluidColorR = ((fluidColor >> 16) & 0xFF) / 255f;
        float fluidColorG = ((fluidColor >> 8) & 0xFF) / 255f;
        float fluidColorB = ((fluidColor) & 0xFF) / 255f;
        //noinspection deprecation
        GlStateManager.color4f(fluidColorR, fluidColorG, fluidColorB, fluidColorA);
        int xPos = 163;
        int ySize = Math.round(81 * pct);
        int yPos = 16 + 81 - ySize;
        RenderHelper.repeatBlit(xPos, yPos, 16, 16, 17, ySize, sprite);
        //noinspection deprecation
        GlStateManager.color4f(1, 1, 1, 1);

        this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_APOTHECARY_GUI);
        this.blit(xPos, 16, this.xSize, 0, 17, 81);

        this.renderHoveredToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        if (mouseX >= 163 && mouseX <= 179 &&
        mouseY >= 16 && mouseY <= 96) {
            String fluid = this.tile.getFluidInventory().getFluidAmount() + " / " + this.tile.getFluidInventory().getCapacity() + " mB";
            this.renderTooltip(fluid, mouseX, mouseY);
        }
    }
}
