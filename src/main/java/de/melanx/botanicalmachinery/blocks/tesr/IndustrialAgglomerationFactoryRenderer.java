package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.render.block.RotatedBlockRenderer;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.helper.RenderHelper;

import javax.annotation.Nonnull;

public class IndustrialAgglomerationFactoryRenderer extends RotatedBlockRenderer<BlockEntityIndustrialAgglomerationFactory> {

    @Override
    protected void doRender(@Nonnull BlockEntityIndustrialAgglomerationFactory tile, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.industrialAgglomerationFactory)
            return;

        double progressLeft = 1 - (tile.getProgress() / (double) tile.getMaxProgress());

        this.renderStack(tile.getInventory().getStackInSlot(0), poseStack, buffer, partialTick, progressLeft, 0, light);
        this.renderStack(tile.getInventory().getStackInSlot(1), poseStack, buffer, partialTick, progressLeft, 120, light);
        this.renderStack(tile.getInventory().getStackInSlot(2), poseStack, buffer, partialTick, progressLeft, 240, light);

        if (!tile.getInventory().getStackInSlot(3).isEmpty()) {
            float time = ClientTickHandler.ticksInGame + partialTick;

            poseStack.pushPose();
            poseStack.translate(0.5, 11.2 / 16d, 0.5);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-time));
            poseStack.translate(0, 0.075 * Math.sin(time / 5d), 0);

            Minecraft.getInstance().getItemRenderer().renderStatic(tile.getInventory().getStackInSlot(3), ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, (int) tile.getBlockPos().asLong());

            poseStack.popPose();
        }

        if (tile.getProgress() > 0) {
            poseStack.pushPose();
            poseStack.translate(6.2 / 16, 4.7 / 16, 6.2 / 16);
            poseStack.scale(3.6f / 16, 3.6f / 16, 3.6f / 16);

            float alphaMod = 50000 * (tile.getProgress() / (float) tile.getMaxProgress());
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
            poseStack.translate(0, 0, -0.18850000202655792);
            float alpha = (float) ((Math.sin((ClientTickHandler.ticksInGame + partialTick) / 8) + 1) / 5 + 0.6) * alphaMod;
            VertexConsumer vertex = buffer.getBuffer(vazkii.botania.client.core.helper.RenderHelper.TERRA_PLATE);
            RenderHelper.renderIconFullBright(poseStack, vertex, MiscellaneousModels.INSTANCE.terraPlateOverlay.sprite(), alpha);

            poseStack.popPose();
        }
    }

    private void renderStack(ItemStack stack, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, float partialTick, double progressLeft, float angle, int light) {
        if (!stack.isEmpty()) {
            double progressLeftScaled = progressLeft * 0.95;
            float time = ClientTickHandler.ticksInGame + partialTick;

            poseStack.pushPose();
            poseStack.translate(0.5, (10.4 - (4 * progressLeft)) / 16d, 0.5);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(angle + time));
            poseStack.translate(progressLeftScaled * 1.125, 0, progressLeftScaled * 0.25);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90f));
            poseStack.translate(0, 0.075 * Math.sin((time + angle) / 5d), 0);

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
            
            poseStack.popPose();
        }
    }
}
