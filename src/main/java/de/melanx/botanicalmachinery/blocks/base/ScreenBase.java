package de.melanx.botanicalmachinery.blocks.base;

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
    public void init(@Nonnull Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
        super.init(p_init_1_, p_init_2_, p_init_3_);
        this.relX = (p_init_2_ - this.xSize) / 2;
        this.relY = (p_init_3_ - this.ySize) / 2;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.manaBar.guiTop = this.guiTop;
        this.manaBar.guiLeft = this.guiLeft;
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.manaBar.renderHoveredToolTip(mouseX, mouseY, ((TileBase) this.getContainer().tile).getCurrentMana());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, Color.DARK_GRAY.getRGB());
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());
    }

    public void drawDefaultGuiBackgroundLayer(ResourceLocation screenLocation) {
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(screenLocation);
        this.blit(this.relX, this.relY, 0, 0, this.xSize, this.ySize);
        this.manaBar.draw(((TileBase) this.container.tile).getCurrentMana());
    }

    public void drawDefaultGuiBackgroundLayer(ResourceLocation screenLocation, int crossX, int crossY) {
        this.drawDefaultGuiBackgroundLayer(screenLocation);

        BlockPos tilePos = this.container.getPos();
        TileEntity tile = this.container.getWorld().getTileEntity(tilePos);
        if (tile instanceof TileBase && !((TileBase) tile).hasValidRecipe()) {
            int x = this.relX + crossX;
            int y = this.relY + crossY;

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            //noinspection ConstantConditions
            this.minecraft.getTextureManager().bindTexture(LibResources.HUD);
            this.blit(x, y, 0, 0, 13, 13);

            RenderSystem.disableLighting();
            RenderSystem.disableBlend();
        }
    }
}
