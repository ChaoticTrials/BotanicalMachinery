package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalApothecary;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.moddingx.libx.render.RenderHelper;
import org.moddingx.libx.render.block.RotatedBlockRenderer;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;

public class MechanicalApothecaryRenderer extends RotatedBlockRenderer<BlockEntityMechanicalApothecary> {

    @Override
    protected void doRender(@Nonnull BlockEntityMechanicalApothecary tile, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.mechanicalApothecary)
            return;

        if (!tile.getInventory().getStackInSlot(0).isEmpty()) {
            float time = ClientTickHandler.ticksInGame + partialTick;

            poseStack.pushPose();
            poseStack.translate(0.5, 14.3 / 16, 0.5);
            poseStack.scale(6 / 16f, 6 / 16f, 6 / 16f);
            poseStack.mulPose(Axis.YP.rotationDegrees(time / 1.3f));

            ItemStack stack = tile.getInventory().getStackInSlot(0);

            if (tile.getProgress() > 0) {
                int progress = tile.getProgress();
                if (progress > tile.getMaxProgress() / 2) {
                    progress = (tile.getMaxProgress() / 2) - Math.abs((tile.getMaxProgress() / 2) - progress);
                    stack = tile.getCurrentOutput();
                }
                double amount = progress / (tile.getMaxProgress() / 2d);
                poseStack.translate(0, -amount, 0);
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, tile.getLevel(), (int) tile.getBlockPos().asLong());

            poseStack.popPose();
        }

        double fluidAmount = (tile.getFluidInventory().getFluidAmount() - ((tile.getProgress() / (double) (tile.getMaxProgress() == 0 ? 1 : tile.getMaxProgress())) * 1000d)) / (double) tile.getFluidInventory().getCapacity();

        if (tile.getFluidInventory().getFluidAmount() > 0) {
            poseStack.pushPose();
            poseStack.translate(4 / 16d, (10 + (fluidAmount * 3.8)) / 16, 4 / 16d);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);

            FluidStack fluidStack = tile.getFluidInventory().getFluid();
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidTypeExtensions.getStillTexture(fluidStack));

            int fluidColor = fluidTypeExtensions.getTintColor(fluidStack);

            VertexConsumer vertex = buffer.getBuffer(Sheets.translucentCullBlockSheet());
            RenderHelper.renderIconColored(poseStack, vertex, 0, 0, sprite, 8, 8, 1.0F, fluidColor, light, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }

        int items = 0;
        for (int slot = 1; slot < 17; slot++) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty()) {
                items += 1;
            }
        }

        double offsetPerPetal = 360d / items;
        double flowerTicks = (double) ((float) ClientTickHandler.ticksInGame + partialTick) / 2;

        poseStack.pushPose();
        poseStack.translate(0.5, (10 + ((fluidAmount * 3.8) / 1.5)) / 16, 0.5);
        poseStack.scale(0.125f, 0.125f, 0.125f);

        int nextIdx = 0;
        boolean hasFluid = tile.getFluidInventory().getFluidAmount() > 0;

        for (int slot = 1; slot < 17; slot++) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty()) {
                int i = nextIdx++;

                double offset = offsetPerPetal * i;
                double deg;
                if (hasFluid) {
                    deg = ((flowerTicks / 0.25) % 360) + offset;
                } else {
                    deg = offset;
                }
                double rad = deg * Math.PI / 180;

                double radiusX;
                double radiusZ;
                if (hasFluid) {
                    radiusX = 1.2000000476837158 + 0.10000000149011612 * Math.sin(flowerTicks / 6);
                    radiusZ = 1.2000000476837158 + 0.10000000149011612 * Math.cos(flowerTicks / 6);
                } else {
                    radiusX = 1.2000000476837158 + 0.10000000149011612;
                    radiusZ = 1.2000000476837158 + 0.10000000149011612;
                }

                double x = radiusX * Math.cos(rad);
                double z = radiusZ * Math.sin(rad);
                double y = hasFluid ? (float) Math.cos((flowerTicks + (double) (50 * i)) / 5.0D) / 10.0F : 0;

                poseStack.pushPose();
                poseStack.translate(x, y, z);

                poseStack.translate(0.0625f, 0.0625f, 0.0625f);
                if (hasFluid) {
                    float xRotate = (float) Math.sin(flowerTicks * 0.25) / 2;
                    float yRotate = (float) Math.max(0.6000000238418579, Math.sin(flowerTicks * 0.10000000149011612) / 2 + 0.5);
                    float zRotate = (float) Math.cos(flowerTicks * 0.25) / 2;
                    Vector3f rotationAxis = new Vector3f(xRotate, yRotate, zRotate);
                    float angleInRadians = (float) Math.toRadians(deg);
                    Quaternionf quaternion = new Quaternionf().rotateAxis(angleInRadians, rotationAxis);
                    poseStack.mulPose(quaternion);
                } else {
                    poseStack.mulPose((Axis.XP.rotationDegrees(90)));
                }
                poseStack.translate(-0.0625f, -0.0625f, -0.0625f);

                ItemStack stack = tile.getInventory().getStackInSlot(slot);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, poseStack, buffer, tile.getLevel(), (int) tile.getBlockPos().asLong() + slot);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}
