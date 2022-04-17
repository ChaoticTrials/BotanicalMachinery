package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import io.github.noeppi_noeppi.libx.render.block.RotatedBlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.helper.RenderHelper;

import javax.annotation.Nonnull;

public class MechanicalManaPoolRenderer extends RotatedBlockRenderer<BlockEntityMechanicalManaPool> {

    public static final double INNER_POOL_HEIGHT = 4.5 / 16;
    public static final double POOL_BOTTOM_HEIGHT = 1.15 / 16;

    @Override
    protected void doRender(@Nonnull BlockEntityMechanicalManaPool tile, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.mechanicalManaPool)
            return;

        ItemStack catalystStack = tile.getInventory().getStackInSlot(0);
        if (!catalystStack.isEmpty() && catalystStack.getItem() instanceof BlockItem && ((BlockItem) catalystStack.getItem()).getBlock() instanceof IPoolOverlayProvider catalyst) {
            ResourceLocation spriteId = catalyst.getIcon(tile.getLevel(), tile.getBlockPos());
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteId);

            poseStack.pushPose();
            poseStack.translate(2 / 16d, POOL_BOTTOM_HEIGHT, 2 / 16d);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);

            float alpha = (float) ((Math.sin((double) ((float) ClientTickHandler.ticksInGame + partialTick) / 20.0D) + 1.0D) * 0.3D + 0.2D);

            VertexConsumer vertex = buffer.getBuffer(RenderHelper.ICON_OVERLAY);
            RenderHelper.renderIcon(poseStack, vertex, 0, 0, sprite, 12, 12, alpha);

            poseStack.popPose();
        }

        if (tile.getCurrentMana() > 0) {
            double amount = tile.getCurrentMana() / (double) tile.getManaCap();

            poseStack.pushPose();
            poseStack.translate(3 / 16d, POOL_BOTTOM_HEIGHT + (amount * INNER_POOL_HEIGHT), 3 / 16d);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);

            VertexConsumer vertex = buffer.getBuffer(RenderHelper.MANA_POOL_WATER);
            RenderHelper.renderIcon(poseStack, vertex, 0, 0, MiscellaneousModels.INSTANCE.manaWater.sprite(), 10, 10, 1.0F);

            poseStack.popPose();
        }

        ItemStack input = tile.getInventory().getStackInSlot(1);
        ItemStack output = tile.getInventory().getStackInSlot(2);

        if (!input.isEmpty() || !output.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(5 / 16d, 7 / 16d, 8 / 16d);

            if (!output.isEmpty()) {
                poseStack.pushPose();
                poseStack.scale(7 / 16f, 7 / 16f, 7 / 16f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame + partialTick));
                Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, (int) tile.getBlockPos().asLong());
                poseStack.popPose();
            }

            poseStack.translate(6 / 16d, 0, 0);

            if (!input.isEmpty()) {
                poseStack.pushPose();
                poseStack.scale(7 / 16f, 7 / 16f, 7 / 16f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame % 360));
                Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, (int) tile.getBlockPos().asLong());
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
