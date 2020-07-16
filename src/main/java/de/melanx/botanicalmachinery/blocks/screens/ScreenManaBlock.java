package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBlock;
import de.melanx.botanicalmachinery.blocks.tiles.IManaMachineTile;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBlock;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.gui.ManaBar;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class ScreenManaBlock extends ContainerScreen<ContainerManaBlock> {

    private final ManaBar manaBar;

    public ScreenManaBlock(ContainerManaBlock container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.manaBar = new ManaBar(this, ((IManaMachineTile) container.tile).getManaCap());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        manaBar.guiTop = this.guiTop;
        manaBar.guiLeft = this.guiLeft;
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.manaBar.renderHoveredToolTip(mouseX, mouseY, ((TileManaBlock) this.getContainer().tile).getCurrentMana());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(LibResources.MANA_BLOCK_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
        BlockPos tilePos = this.getContainer().getPos();
        TileEntity tile = this.getContainer().getWorld().getTileEntity(tilePos);
        if (tile instanceof IManaMachineTile && !((IManaMachineTile) tile).hasValidRecipe()) {
            int x = relX + 81;
            int y = relY + 37;

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.minecraft.getTextureManager().bindTexture(LibResources.HUD);
            this.blit(x, y, 0, 0, 13, 13);

            RenderSystem.disableLighting();
            RenderSystem.disableBlend();
        }
        manaBar.draw(((TileManaBlock) this.getContainer().tile).getCurrentMana());
    }
}
