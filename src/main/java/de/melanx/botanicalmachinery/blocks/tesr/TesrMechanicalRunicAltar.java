package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.config.ClientConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;

public class TesrMechanicalRunicAltar extends HorizontalRotatedTesr<TileMechanicalRunicAltar> {

    private final ModelRenderer spinningCube = new ModelRenderer(64, 32, 42, 0);

    public TesrMechanicalRunicAltar(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileMechanicalRunicAltar tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (!ClientConfig.everything.get() || !ClientConfig.runicAltar.get())
            return;

        ItemStack livingRockStack = tile.getInventory().getStackInSlot(0);
        if (!livingRockStack.isEmpty() && livingRockStack.getItem() instanceof BlockItem) {
            BlockState state = ((BlockItem) livingRockStack.getItem()).getBlock().getDefaultState();

            matrixStack.push();
            matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
            matrixStack.translate(6.5, 10, 6.5);
            matrixStack.scale(3, 3, 3);

            matrixStack.translate(0.5, 0, 0.5);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-(ClientTickHandler.ticksInGame + partialTicks)));
            matrixStack.translate(-0.5, 0, -0.5);

            //noinspection deprecation
            Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(state, matrixStack, buffer, 200, OverlayTexture.NO_OVERLAY);

            matrixStack.pop();
        }

        double progressLeft = 1 - ((tile.getProgress() / (double) tile.getMaxProgress()) * 0.9);

        int items = 0;
        for (int slot : tile.getInventory().getInputSlots()) {
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
        for (int slot : tile.getInventory().getInputSlots()) {
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

                matrixStack.push();
                matrixStack.translate(0.5, 10.8 / 16d, 0.5);
                matrixStack.scale(0.3f, 0.3f, 0.3f);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(angles[angleIdx] + time));
                matrixStack.translate(travelCenter * 1.125, 0, travelCenter * 0.25);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90f));
                matrixStack.translate(0, 0.075 * Math.sin((time + (angleIdx * 10)) / 5d), 0);
                if (shrink)
                    matrixStack.scale(0.3f, 0.3f, 0.3f);

                ItemStack stack = tile.getInventory().getStackInSlot(slot);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

                matrixStack.pop();
            }
        }
    }
}
