package de.melanx.botanicalmachinery.blocks.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.gui.ManaBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;

public abstract class ScreenBase<X extends ContainerBase<?>> extends ContainerScreen<X> {
    public final ManaBar manaBar;
    public int relX;
    public int relY;
    public final ContainerBase<?> container;

    public ScreenBase(X container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.manaBar = new ManaBar(this, ((IManaMachineTile) container.tile).getManaCap());
        this.container = this.getContainer();
    }

    @Override
    public void init(@Nonnull Minecraft mc, int x, int y) {
        super.init(mc, x, y);
        this.relX = (x - this.xSize) / 2;
        this.relY = (y - this.ySize) / 2;
    }

    @Override
    public void render(@Nonnull MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.manaBar.guiTop = this.guiTop;
        this.manaBar.guiLeft = this.guiLeft;
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(ms, mouseX, mouseY);
        this.manaBar.renderHoveredToolTip(ms, mouseX, mouseY, ((TileBase) this.getContainer().tile).getCurrentMana());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack ms, int mouseX, int mouseY) {
        String s = this.title.getString();
        this.font.drawString(ms, s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, Color.DARK_GRAY.getRGB());
        this.font.drawString(ms, this.playerInventory.getDisplayName().getString(), 8.0F, (float) (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());
    }

    public void drawDefaultGuiBackgroundLayer(MatrixStack ms, ResourceLocation screenLocation) {
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(screenLocation);
        this.blit(ms, this.relX, this.relY, 0, 0, this.xSize, this.ySize);
        this.manaBar.draw(ms, ((TileBase) this.container.tile).getCurrentMana());
    }

    public void drawDefaultGuiBackgroundLayer(MatrixStack ms, ResourceLocation screenLocation, int crossX, int crossY) {
        this.drawDefaultGuiBackgroundLayer(ms, screenLocation);

        BlockPos tilePos = this.container.getPos();
        TileEntity tile = this.container.getWorld().getTileEntity(tilePos);
        if (tile instanceof TileBase && !((TileBase) tile).hasValidRecipe()) {
            int x = this.relX + crossX;
            int y = this.relY + crossY;

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            //noinspection ConstantConditions
            this.minecraft.getTextureManager().bindTexture(LibResources.HUD);
            this.blit(ms, x, y, 0, 0, 13, 13);

            RenderSystem.disableLighting();
            RenderSystem.disableBlend();
        }
    }
}
