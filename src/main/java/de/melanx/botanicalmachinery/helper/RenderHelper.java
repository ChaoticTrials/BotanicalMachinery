package de.melanx.botanicalmachinery.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

    /**
     * Repeatedly blits a texture
     * @param x x coordinate of topleft corner
     * @param y y coordinate of topleft corner
     * @param texWidth with of one texture element. If this is lower than displayWidth the texture is looped.
     * @param texHeight height of one texture element. If this is lower than displayHeight the texture is looped.
     * @param displayWidth the width of the blit
     * @param displayHeight the height of the blit
     * @param sprite A texture sprite
     */
    public static void repeatBlit(int x, int y, int texWidth, int texHeight, int displayWidth, int displayHeight, TextureAtlasSprite sprite) {
        repeatBlit(x, y, texWidth, texHeight, displayWidth, displayHeight, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public static void repeatBlit(int x, int y, int texWidth, int texHeight, int displayWidth, int displayHeight, float minU, float maxU, float minV, float maxV) {
        int pixelsRenderedX = 0;
        while (pixelsRenderedX < displayWidth) {
            int pixelsNowX = Math.min(texWidth, displayWidth - pixelsRenderedX);
            float maxUnow = maxU;
            if (pixelsNowX < texWidth) {
                maxUnow = minU + ((maxU - minU) * (pixelsNowX / (float) texWidth));
            }

            int pixelsRenderedY = 0;
            while (pixelsRenderedY < displayHeight) {
                int pixelsNowY = Math.min(texHeight, displayHeight - pixelsRenderedY);
                float maxVnow = maxV;
                if (pixelsNowY < texHeight) {
                    maxVnow = minV + ((maxV - minV) * (pixelsNowY / (float) texHeight));
                }

                AbstractGui.innerBlit(x + pixelsRenderedX, x + pixelsRenderedX + pixelsNowX,
                        y + pixelsRenderedY, y + pixelsRenderedY + pixelsNowY,
                        0, minU, maxUnow, minV, maxVnow);

                pixelsRenderedY += pixelsNowY;
            }

            pixelsRenderedX += pixelsNowX;
        }
    }
}
