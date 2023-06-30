package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityAlfheimMarket;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.render.block.RotatedBlockRenderer;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.common.block.BotaniaBlocks;

import javax.annotation.Nonnull;

public class AlfheimMarketRenderer extends RotatedBlockRenderer<BlockEntityAlfheimMarket> {

    @Override
    public void doRender(@Nonnull BlockEntityAlfheimMarket tile, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.alfheimMarket)
            return;

        poseStack.pushPose();
        poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        poseStack.translate(3.2, 2, 3.6);
        poseStack.scale(3.6f, 3.6f, 3.6f);
        //noinspection deprecation
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BotaniaBlocks.naturaPylon.defaultBlockState(), poseStack, buffer, light, overlay);
        poseStack.translate(1 + (2 / 3.6), 0, 0);
        //noinspection deprecation
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BotaniaBlocks.naturaPylon.defaultBlockState(), poseStack, buffer, light, overlay);
        poseStack.popPose();

        if (tile.getCurrentMana() > 0) {
            poseStack.pushPose();
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
            poseStack.translate(6.8, 1, 8.8);
            poseStack.scale(2.4f, 2.4f, 2.4f);

            poseStack.translate(-1.0D, 1.0D, 0.25D);
            float alpha = (float) Math.min(1.0D, (Math.sin((double) ((float) ClientTickHandler.ticksInGame + partialTick) / 8.0D) + 1.0D) / 7.0D + 0.7D);

            this.renderPortal(poseStack, buffer, MiscellaneousModels.INSTANCE.alfPortalTex.sprite(), 0, 0, 3, 3, alpha, overlay);
            poseStack.translate(3.0D, 0.0D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            this.renderPortal(poseStack, buffer, MiscellaneousModels.INSTANCE.alfPortalTex.sprite(), 0, 0, 3, 3, alpha, overlay);

            poseStack.popPose();
        }

        if (tile.getProgress() > 0) {
            double progress = (tile.getProgress() - (tile.getMaxProgress() / 2d)) / (tile.getMaxProgress() / 2d);
            ItemStack stack = progress < 0 ? tile.getCurrentInput() : tile.getCurrentOutput();
            if (!stack.isEmpty()) {
                double yPos = Math.pow(progress, 2);
                double zPos = -(progress * 0.75);

                poseStack.pushPose();
                poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
                poseStack.translate(8, 4.6, 8.8);
                poseStack.scale(5.4f, 5.4f, 5.4f);
                poseStack.translate(0, yPos, zPos);
                poseStack.scale(-1, 1, -1);
                poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, (int) tile.getBlockPos().asLong());
                poseStack.popPose();
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void renderPortal(PoseStack poseStack, MultiBufferSource buffer, TextureAtlasSprite sprite, int x, int y, int width, int height, float alpha, int overlay) {
        VertexConsumer vertex = buffer.getBuffer(Sheets.translucentCullBlockSheet());
        Matrix4f model = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        vertex.vertex(model, (float) x, (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU0(), sprite.getV1()).overlayCoords(overlay).uv2(LightTexture.pack(15, 15)).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.vertex(model, (float) (x + width), (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU1(), sprite.getV1()).overlayCoords(overlay).uv2(LightTexture.pack(15, 15)).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.vertex(model, (float) (x + width), (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU1(), sprite.getV0()).overlayCoords(overlay).uv2(LightTexture.pack(15, 15)).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.vertex(model, (float) x, (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU0(), sprite.getV0()).overlayCoords(overlay).uv2(LightTexture.pack(15, 15)).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
    }
}
