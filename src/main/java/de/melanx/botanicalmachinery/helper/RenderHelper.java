package de.melanx.botanicalmachinery.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RenderHelper {

    public static void renderFadedItem(Screen screen, List<Item> items, int x, int y) {
        if (items.isEmpty()) {
            items = Collections.singletonList(Items.AIR);
        }
        int idx = (items.size() + ((ClientTickHandler.ticksInGame / 20) % items.size())) % items.size();
        renderFadedItem(screen, items.get(idx), x, y);
    }

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
     *
     * @param x             x coordinate of topleft corner
     * @param y             y coordinate of topleft corner
     * @param texWidth      width of one texture element. If this is lower than displayWidth the texture is looped.
     * @param texHeight     height of one texture element. If this is lower than displayHeight the texture is looped.
     * @param displayWidth  the width of the blit
     * @param displayHeight the height of the blit
     * @param sprite        A texture sprite
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

    public static void renderItemTinted(ItemStack stack, ItemCameraTransforms.TransformType transformType, int light, int overlay, MatrixStack matrixStack, IRenderTypeBuffer buffer, float r, float g, float b) {
        if (!stack.isEmpty()) {
            boolean isGui = transformType == ItemCameraTransforms.TransformType.GUI;
            boolean isFixed = isGui || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;

            IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, false);

            matrixStack.push();
            matrixStack.translate(-0.5D, -0.5D, -0.5D);

            if (!model.isBuiltInRenderer() && (stack.getItem() != Items.TRIDENT || isFixed)) {
                RenderType type = RenderTypeLookup.getRenderType(stack);
                if (isGui && Objects.equals(type, Atlases.getTranslucentBlockType())) {
                    type = Atlases.getTranslucentCullBlockType();
                }

                IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(buffer, type, true, stack.hasEffect());
                renderTintedModel(model, light, overlay, matrixStack, ivertexbuilder, r, g, b);
            } else {
                //noinspection deprecation
                GlStateManager.color4f(r, g, b, 1);
                stack.getItem().getItemStackTileEntityRenderer().render(stack, matrixStack, buffer, light, overlay);
                //noinspection deprecation
                GlStateManager.color4f(1, 1, 1, 1);
            }

            matrixStack.pop();
        }
    }

    private static void renderTintedModel(IBakedModel model, int light, int overlay, MatrixStack matrixStack, IVertexBuilder buffer, float r, float g, float b) {
        Random random = new Random();

        for (Direction direction : Direction.values()) {
            random.setSeed(42);
            //noinspection deprecation
            renderTintedQuads(matrixStack, buffer, model.getQuads(null, direction, random), light, overlay, r, g, b);
        }

        random.setSeed(42);
        //noinspection deprecation
        renderTintedQuads(matrixStack, buffer, model.getQuads(null, null, random), light, overlay, r, g, b);
    }

    private static void renderTintedQuads(MatrixStack matrixStack, IVertexBuilder buffer, List<BakedQuad> quads, int light, int overlay, float r, float g, float b) {
        MatrixStack.Entry entry = matrixStack.getLast();

        for (BakedQuad bakedquad : quads) {
            buffer.addVertexData(entry, bakedquad, r, g, b, light, overlay, true);
        }
    }

    public static void renderIconColored(MatrixStack matrixStack, IVertexBuilder buffer, float x, float y, TextureAtlasSprite sprite, float width, float height, float alpha, int color, int light, int overlay) {
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        Matrix4f mat = matrixStack.getLast().getMatrix();
        buffer.pos(mat, x, y + height, 0.0F).color(red, green, blue, (int)(alpha * 255.0F)).tex(sprite.getMinU(), sprite.getMaxV()).overlay(overlay).lightmap(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(mat, x + width, y + height, 0.0F).color(red, green, blue, (int)(alpha * 255.0F)).tex(sprite.getMaxU(), sprite.getMaxV()).overlay(overlay).lightmap(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(mat, x + width, y, 0.0F).color(red, green, blue, (int)(alpha * 255.0F)).tex(sprite.getMaxU(), sprite.getMinV()).overlay(overlay).lightmap(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(mat, x, y, 0.0F).color(red, green, blue, (int)(alpha * 255.0F)).tex(sprite.getMinU(), sprite.getMinV()).overlay(overlay).lightmap(light).normal(0.0F, 0.0F, 1.0F).endVertex();
    }
}
