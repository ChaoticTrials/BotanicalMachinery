package de.melanx.botanicalmachinery.blocks.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.gui.ManaBar;
import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.awt.*;

public abstract class ScreenBase<X extends BlockEntityMenu<?>> extends AbstractContainerScreen<X> {

    public final ManaBar manaBar;
    public int relX;
    public int relY;

    public ScreenBase(X menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        if (this.menu.getBlockEntity() instanceof BotanicalTile botanicalTile) {
            this.manaBar = new ManaBar(this, botanicalTile.getManaCap());
        } else {
            this.manaBar = new ManaBar(this, 0);
        }
    }

    @Override
    public void init(@Nonnull Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        this.relX = (width - this.imageWidth) / 2;
        this.relY = (height - this.imageHeight) / 2;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.manaBar.guiTop = this.topPos;
        this.manaBar.guiLeft = this.leftPos;
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
        if (this.menu.getBlockEntity() instanceof BotanicalTile botanicalTile) {
            this.manaBar.renderHoveredToolTip(poseStack, mouseX, mouseY, botanicalTile.getCurrentMana());
        }
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        String s = this.title.getString();
        this.font.draw(poseStack, s, (float) (this.imageWidth / 2 - this.font.width(s) / 2), 6.0F, Color.DARK_GRAY.getRGB());
        this.font.draw(poseStack, this.playerInventoryTitle.getString(), 8.0F, (float) (this.imageHeight - 96 + 2), Color.DARK_GRAY.getRGB());
    }

    public void drawDefaultGuiBackgroundLayer(PoseStack poseStack, ResourceLocation screenLocation) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, screenLocation);
        this.blit(poseStack, this.relX, this.relY, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.getBlockEntity() instanceof BotanicalTile botanicalTile) {
            this.manaBar.draw(poseStack, botanicalTile.getCurrentMana());
        }
    }
}
