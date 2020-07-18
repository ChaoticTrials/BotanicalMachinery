package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerIndustrialAgglomarationFactory;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.item.ModItems;

public class ScreenIndustrialAgglomarationFactory extends ScreenBase<ContainerIndustrialAgglomarationFactory> {
    public ScreenIndustrialAgglomarationFactory(ContainerIndustrialAgglomarationFactory container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.ySize = 195;
        this.manaBar.x -= 5;
        this.manaBar.y += 23;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.INDUSTRIAL_AGGLOMARATION_FACTORY_GUI, 81, 37);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.manaSteel), relX + 53, relY + 47);
        GlStateManager.enableBlend();
        GlStateManager.disableDepthTest();
        this.minecraft.getTextureManager().bindTexture(LibResources.HUD);
        RenderHelper.drawTexturedModalRect(relX + 53, relY + 47, 16, 0, 16, 16);
    }

}
