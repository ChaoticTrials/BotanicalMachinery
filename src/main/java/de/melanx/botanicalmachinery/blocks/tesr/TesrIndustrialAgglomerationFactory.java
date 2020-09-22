package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.IconHelper;

import javax.annotation.Nonnull;

public class TesrIndustrialAgglomerationFactory extends HorizontalRotatedTesr<TileIndustrialAgglomerationFactory> {

    public TesrIndustrialAgglomerationFactory(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileIndustrialAgglomerationFactory tile, float partialTicks, @Nonnull MatrixStack ms, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (!ClientConfig.everything.get() || !ClientConfig.agglomerationFactory.get())
            return;

        double progressLeft = 1 - (tile.getProgress() / (double) tile.getMaxProgress());

        this.renderStack(tile.getInventory().getStackInSlot(0), ms, buffer, partialTicks, progressLeft, 0);
        this.renderStack(tile.getInventory().getStackInSlot(1), ms, buffer, partialTicks, progressLeft, 120);
        this.renderStack(tile.getInventory().getStackInSlot(2), ms, buffer, partialTicks, progressLeft, 240);

        if (!tile.getInventory().getStackInSlot(3).isEmpty()) {
            float time = ClientTickHandler.ticksInGame + partialTicks;

            ms.push();
            ms.translate(0.5, 11.2 / 16d, 0.5);
            ms.scale(0.3f, 0.3f, 0.3f);
            ms.rotate(Vector3f.YP.rotationDegrees(-time));
            ms.translate(0, 0.075 * Math.sin(time / 5d), 0);

            Minecraft.getInstance().getItemRenderer().renderItem(tile.getInventory().getStackInSlot(3), ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, ms, buffer);

            ms.pop();
        }

        if (tile.getProgress() > 0) {
            ms.push();
            ms.translate(6.2 / 16, 4.7 / 16, 6.2 / 16);
            ms.scale(3.6f / 16, 3.6f / 16, 3.6f / 16);

            float alphaMod = 50000 * (tile.getProgress() / (float) tile.getMaxProgress());
            ms.rotate(Vector3f.XP.rotationDegrees(90));
            ms.translate(0, 0, -0.18850000202655792);
            float alpha = (float) ((Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 8) + 1) / 5 + 0.6) * alphaMod;
            IVertexBuilder vertex = buffer.getBuffer(vazkii.botania.client.core.helper.RenderHelper.TERRA_PLATE);
            IconHelper.renderIcon(ms, vertex, 0, 0, MiscellaneousIcons.INSTANCE.terraPlateOverlay.getSprite(), 1, 1, alpha);

            ms.pop();
        }
    }

    private void renderStack(ItemStack stack, @Nonnull MatrixStack ms, @Nonnull IRenderTypeBuffer buffer, float partialTicks, double progressLeft, float angle) {
        if (!stack.isEmpty()) {
            double progressLeftScaled = progressLeft * 0.95;
            float time = ClientTickHandler.ticksInGame + partialTicks;
            float colorTint = (float) Math.min(1, progressLeft * 1.2);

            ms.push();
            ms.translate(0.5, (10.4 - (4 * progressLeft)) / 16d, 0.5);
            ms.scale(0.3f, 0.3f, 0.3f);
            ms.rotate(Vector3f.YP.rotationDegrees(angle + time));
            ms.translate(progressLeftScaled * 1.125, 0, progressLeftScaled * 0.25);
            ms.rotate(Vector3f.YP.rotationDegrees(90f));
            ms.translate(0, 0.075 * Math.sin((time + angle) / 5d), 0);

            RenderHelper.renderItemTinted(stack, ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, ms, buffer, colorTint, 1, colorTint);

            ms.pop();
        }
    }
}
