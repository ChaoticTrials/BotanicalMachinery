package de.melanx.botanicalmachinery.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RenderHelper {

    public static void renderFadedItem(Screen screen, Item item, int x, int y) {
        screen.getMinecraft().getItemRenderer().renderItemIntoGUI(new ItemStack(item), x, y);
        GlStateManager.enableBlend();
        GlStateManager.disableDepthTest();
        screen.getMinecraft().getTextureManager().bindTexture(LibResources.HUD);
        //noinspection deprecation
        GlStateManager.color4f(1, 1, 1, 1);
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(x, y, 16, 0, 16, 16);
    }
}
