package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import de.melanx.botanicalmachinery.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.common.block.ModBlocks;

import javax.annotation.Nonnull;

public class TesrAlfheimMarket extends HorizontalRotatedTesr<TileAlfheimMarket> {

    public TesrAlfheimMarket(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void doRender(@Nonnull TileAlfheimMarket tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (!ClientConfig.everything.get() || !ClientConfig.alfheimMarket.get())
            return;

        matrixStack.push();
        matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        matrixStack.translate(3.2, 2, 3.6);
        matrixStack.scale(3.6f, 3.6f, 3.6f);
        //noinspection deprecation
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(ModBlocks.naturaPylon.getDefaultState(), matrixStack, buffer, light, overlay);
        matrixStack.translate(1 + (2 / 3.6), 0, 0);
        //noinspection deprecation
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(ModBlocks.naturaPylon.getDefaultState(), matrixStack, buffer, light, overlay);
        matrixStack.pop();

        if (tile.getCurrentMana() > 0) {
            matrixStack.push();
            matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
            matrixStack.translate(6.8, 1, 8.8);
            matrixStack.scale(2.4f, 2.4f, 2.4f);

            matrixStack.translate(-1.0D, 1.0D, 0.25D);
            float alpha = (float) Math.min(1.0D, (Math.sin((double) ((float) ClientTickHandler.ticksInGame + partialTicks) / 8.0D) + 1.0D) / 7.0D + 0.6D);

            this.renderPortal(matrixStack, buffer, MiscellaneousIcons.INSTANCE.alfPortalTex, 0, 0, 3, 3, alpha, overlay);
            matrixStack.translate(0.0D, 0.0D, 0.5D);
            this.renderPortal(matrixStack, buffer, MiscellaneousIcons.INSTANCE.alfPortalTex, 0, 0, 3, 3, alpha, overlay);

            matrixStack.pop();
        }

        if (tile.getProgress() > 0) {
            double progress = (tile.getProgress() - (tile.getMaxProgress() / 2d)) / (tile.getMaxProgress() / 2d);
            ItemStack stack = progress < 0 ? tile.getCurrentInput() : tile.getCurrentOutput();
            if (!stack.isEmpty()) {
                double yPos = Math.pow(progress, 2);
                double zPos = -(progress * 0.75);

                matrixStack.push();
                matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
                matrixStack.translate(8, 4.6, 8.8);
                matrixStack.scale(5.4f, 5.4f, 5.4f);
                matrixStack.translate(0, yPos, zPos);
                matrixStack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());

                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
                matrixStack.pop();
            }
        }
    }

    private void renderPortal(MatrixStack matrixStack, IRenderTypeBuffer buffer, TextureAtlasSprite sprite, int x, int y, int width, int height, float alpha, int overlay) {
        IVertexBuilder vertex = buffer.getBuffer(Atlases.getTranslucentBlockType());
        Matrix4f model = matrixStack.getLast().getMatrix();
        Matrix3f normal = matrixStack.getLast().getNormal();
        vertex.pos(model, (float) x, (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(sprite.getMinU(), sprite.getMaxV()).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.pos(model, (float) (x + width), (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(sprite.getMaxU(), sprite.getMaxV()).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.pos(model, (float) (x + width), (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(sprite.getMaxU(), sprite.getMinV()).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.pos(model, (float) x, (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(sprite.getMinU(), sprite.getMinV()).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
    }
}
