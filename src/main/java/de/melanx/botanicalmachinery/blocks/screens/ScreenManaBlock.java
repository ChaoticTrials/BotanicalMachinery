package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenManaBlock extends ContainerScreen<ContainerManaBlock> {
    public ScreenManaBlock(ContainerManaBlock container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void func_230450_a_(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(ms);
        this.func_230459_a_(ms, mouseX, mouseY); // renderHoveredToolTip
    }

    @Override
    public void renderDirtBackground(int p_231165_1_) {
        super.renderDirtBackground(p_231165_1_);
    }

    @Override
    protected void func_230451_b_(MatrixStack ms, int p_230451_2_, int p_230451_3_) {
        super.func_230451_b_(ms, p_230451_2_, p_230451_3_);
        drawString(ms, Minecraft.getInstance().fontRenderer, "Slot", 55, 24, 0xffffff);
        drawString(ms, Minecraft.getInstance().fontRenderer, "Slot 2", 109, 24, 0xffffff);
    }


}
