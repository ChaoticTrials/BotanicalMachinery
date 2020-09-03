package de.melanx.botanicalmachinery.blocks.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public abstract class HorizontalRotatedTesr<T extends TileEntity> extends TileEntityRenderer<T> {

    public HorizontalRotatedTesr(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public final void render(@Nonnull T tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        matrixStack.push();
        float f = tile.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle() + 180;
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-f));
        matrixStack.translate(-0.5D, -0.5D, -0.5D);
        this.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        matrixStack.pop();
    }

    protected abstract void doRender(@Nonnull T tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay);
}
