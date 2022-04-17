package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalRunicAltar;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import io.github.noeppi_noeppi.libx.render.block.RotatedBlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;

public class MechanicalRunicAltarRenderer extends RotatedBlockRenderer<BlockEntityMechanicalRunicAltar> {
    
    @Override
    protected void doRender(@Nonnull BlockEntityMechanicalRunicAltar tile, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.mechanicalRunicAltar)
            return;

        ItemStack livingRockStack = tile.getInventory().getStackInSlot(0);
        if (!livingRockStack.isEmpty() && livingRockStack.getItem() instanceof BlockItem) {
            BlockState state = ((BlockItem) livingRockStack.getItem()).getBlock().defaultBlockState();

            poseStack.pushPose();
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
            poseStack.translate(6.5, 10, 6.5);
            poseStack.scale(3, 3, 3);

            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-(ClientTickHandler.ticksInGame + partialTicks)));
            poseStack.translate(-0.5, 0, -0.5);

            //noinspection deprecation
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffer, 200, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }

        double progressLeft = 1 - ((tile.getProgress() / (double) tile.getMaxProgress()) * 0.9);

        int items = 0;
        for (int slot = 1; slot < 17; slot++) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty())
                items += 1;
        }

        float[] angles = new float[items];
        float anglePer = 360f / items;
        float totalAngle = 0;
        for (int i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }

        float time = ClientTickHandler.ticksInGame + partialTicks;

        int nextAngleIdx = 0;
        for (int slot = 1; slot < 17; slot++) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty()) {
                double travelCenter = 1;
                boolean shrink = false;
                if (tile.isSlotUsedCurrently(slot)) {
                    travelCenter = progressLeft;
                } else if (tile.getProgress() > 0) {
                    shrink = true;
                }

                int angleIdx = nextAngleIdx++;
                if (angleIdx >= angles.length)
                    break;

                poseStack.pushPose();
                poseStack.translate(0.5, 10.8 / 16d, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(angles[angleIdx] + time));
                poseStack.translate(travelCenter * 1.125, 0, travelCenter * 0.25);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90f));
                poseStack.translate(0, 0.075 * Math.sin((time + (angleIdx * 10)) / 5d), 0);
                if (shrink) {
                    poseStack.scale(0.3f, 0.3f, 0.3f);
                }

                ItemStack stack = tile.getInventory().getStackInSlot(slot);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, (int) tile.getBlockPos().asLong() + slot);

                poseStack.popPose();
            }
        }
    }
}
