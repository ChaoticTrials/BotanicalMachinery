package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.item.ModItems;

public class ScreenIndustrialAgglomerationFactory extends ScreenBase<ContainerIndustrialAgglomerationFactory> {
    public ScreenIndustrialAgglomerationFactory(ContainerIndustrialAgglomerationFactory container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.ySize = 195;
        this.manaBar.x -= 5;
        this.manaBar.y += 23;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI, 81, 37);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.manaSteel), relX + 61, relY + 83);
        this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.manaDiamond), relX + 80, relY + 83);
        this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.manaPearl), relX + 99, relY + 83);
        GlStateManager.enableBlend();
        GlStateManager.disableDepthTest();
        this.minecraft.getTextureManager().bindTexture(LibResources.HUD);
        RenderHelper.drawTexturedModalRect(relX + 61, relY + 83, 16, 0, 16, 16);
        RenderHelper.drawTexturedModalRect(relX + 80, relY + 83, 16, 0, 16, 16);
        RenderHelper.drawTexturedModalRect(relX + 99, relY + 83, 16, 0, 16, 16);
    }

}
