package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import de.melanx.botanicalmachinery.config.ClientConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TesrMechanicalDaisy extends TileEntityRenderer<TileMechanicalDaisy> {

    private static final float SCALE = 0.3125f;

    public TesrMechanicalDaisy(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(@Nonnull TileMechanicalDaisy tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (!ClientConfig.everything.get() || !ClientConfig.daisy.get())
            return;

        matrixStack.push();
        matrixStack.scale(SCALE, SCALE, SCALE);
        matrixStack.translate(0, 0.125f / SCALE, 0);
        this.renderState(tile.getState(0), 0 / SCALE, 0 / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(1), 0.34375f / SCALE, 0 / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(2), 0.6875f / SCALE, 0 / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(3), 0 / SCALE, 0.34375f / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(4), 0.6875f / SCALE, 0.34375f / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(5), 0 / SCALE, 0.6875f / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(6), 0.34375f / SCALE, 0.6875f / SCALE, matrixStack, buffer, light, overlay);
        this.renderState(tile.getState(7), 0.6875f / SCALE, 0.6875f / SCALE, matrixStack, buffer, light, overlay);
        matrixStack.pop();
    }

    private void renderState(@Nullable BlockState state, float translateX, float translateZ, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (state != null) {
            matrixStack.push();
            matrixStack.translate(translateX, 0, translateZ);
            //noinspection deprecation
            Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(state, matrixStack, buffer, light, overlay);
            matrixStack.pop();
        }
    }
}
