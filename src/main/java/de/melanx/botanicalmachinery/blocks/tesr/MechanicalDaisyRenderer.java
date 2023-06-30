package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalDaisy;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanicalDaisyRenderer implements BlockEntityRenderer<BlockEntityMechanicalDaisy> {

    private static final float SCALE = 0.3125f;

    @Override
    public void render(@Nonnull BlockEntityMechanicalDaisy blockEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.mechanicalDaisy) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.translate(0, 0.125f / SCALE, 0);
        this.renderState(blockEntity.getState(0), 0 / SCALE, 0 / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(1), 0.34375f / SCALE, 0 / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(2), 0.6875f / SCALE, 0 / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(3), 0 / SCALE, 0.34375f / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(4), 0.6875f / SCALE, 0.34375f / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(5), 0 / SCALE, 0.6875f / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(6), 0.34375f / SCALE, 0.6875f / SCALE, poseStack, buffer, light, overlay);
        this.renderState(blockEntity.getState(7), 0.6875f / SCALE, 0.6875f / SCALE, poseStack, buffer, light, overlay);
        poseStack.popPose();
    }

    private void renderState(@Nullable BlockState state, float translateX, float translateZ, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (state != null) {
            poseStack.pushPose();
            poseStack.translate(translateX, 0, translateZ);
            //noinspection deprecation
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffer, light, overlay);
            poseStack.popPose();
        }
    }
}
