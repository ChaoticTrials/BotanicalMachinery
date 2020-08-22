package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.render.tile.RenderTilePool;

import javax.annotation.Nonnull;

public class TesrMechanicalManaPool extends HorizontalRotatedTesr<TileMechanicalManaPool> {

    public static final double INNER_POOL_HEIGHT = 4.5 / 16;
    public static final double POOL_BOTTOM_HEIGHT = 1.15 / 16;

    public TesrMechanicalManaPool(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileMechanicalManaPool tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        ItemStack catalystStack = tile.getInventory().getStackInSlot(0);
        if (!catalystStack.isEmpty() && catalystStack.getItem() instanceof BlockItem && ((BlockItem) catalystStack.getItem()).getBlock() instanceof IPoolOverlayProvider) {
            IPoolOverlayProvider catalyst = (IPoolOverlayProvider) ((BlockItem) catalystStack.getItem()).getBlock();
            TextureAtlasSprite sprite = catalyst.getIcon(tile.getWorld(), tile.getPos());

            matrixStack.push();

            matrixStack.translate(2/16d, POOL_BOTTOM_HEIGHT, 2/16d);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStack.scale(1/16f, 1/16f, 1/16f);

            float alpha = (float)((Math.sin((double)((float) ClientTickHandler.ticksInGame + partialTicks) / 20.0D) + 1.0D) * 0.3D + 0.2D);

            IVertexBuilder vertex = buffer.getBuffer(RenderHelper.ICON_OVERLAY);
            IconHelper.renderIcon(matrixStack, vertex, 0, 0, sprite, 12, 12, alpha);

            matrixStack.pop();
        }

        if (tile.getCurrentMana() > 0) {
            double amount = tile.getCurrentMana() / (double) tile.getManaCap();

            matrixStack.push();
            matrixStack.translate(3/16d, POOL_BOTTOM_HEIGHT + (amount * INNER_POOL_HEIGHT), 3/16d);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStack.scale(1/16f, 1/16f, 1/16f);

            IVertexBuilder vertex = buffer.getBuffer(RenderHelper.MANA_POOL_WATER);
            IconHelper.renderIcon(matrixStack, vertex, 0, 0, MiscellaneousIcons.INSTANCE.manaWater, 10, 10, 1.0F);

            matrixStack.pop();
        }

        ItemStack input = tile.getInventory().getStackInSlot(1);
        ItemStack output = tile.getInventory().getStackInSlot(2);

        if (!input.isEmpty() || !output.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(5 / 16d, 7 / 16d, 8 / 16d);

            if (!output.isEmpty()) {
                matrixStack.push();
                matrixStack.scale(7 / 16f, 7 / 16f, 7 / 16f);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame + partialTicks));
                Minecraft.getInstance().getItemRenderer().renderItem(output, ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
                matrixStack.pop();
            }

            matrixStack.translate(6 / 16d, 0, 0);

            if (!input.isEmpty()) {
                matrixStack.push();
                matrixStack.scale(7 / 16f, 7 / 16f, 7 / 16f);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame % 360));
                Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
                matrixStack.pop();
            }

            matrixStack.pop();
        }
    }
}
