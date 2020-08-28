package de.melanx.botanicalmachinery.blocks.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;

public class TesrMechanicalApothecary extends HorizontalRotatedTesr<TileMechanicalApothecary> {

    public TesrMechanicalApothecary(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileMechanicalApothecary tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (!tile.getInventory().getStackInSlot(0).isEmpty()) {
            float time = ClientTickHandler.ticksInGame + partialTicks;

            matrixStack.push();
            matrixStack.translate(0.5, 14.3 / 16, 0.5);
            matrixStack.scale(6 / 16f, 6 / 16f, 6 / 16f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(time / 1.3f));

            ItemStack stack = tile.getInventory().getStackInSlot(0);

            if (tile.getProgress() > 0) {
                int progress = tile.getProgress();
                if (progress > TileMechanicalApothecary.WORKING_DURATION / 2) {
                    progress = (TileMechanicalApothecary.WORKING_DURATION / 2) - Math.abs((TileMechanicalApothecary.WORKING_DURATION / 2) - progress);
                    stack = tile.getCurrentOutput();
                }
                double amount = progress / (TileMechanicalApothecary.WORKING_DURATION / 2d);
                matrixStack.translate(0, -amount, 0);
            }

            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, 200, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

            matrixStack.pop();
        }

        double fluidAmount = (tile.getFluidInventory().getFluidAmount() - ((tile.getProgress() / (double) TileMechanicalApothecary.WORKING_DURATION) * 1000d)) / (double) tile.getFluidInventory().getCapacity();

        if (tile.getFluidInventory().getFluidAmount() > 0) {
            matrixStack.push();
            matrixStack.translate(4 / 16d, (10 + (fluidAmount * 3.8)) / 16, 4 / 16d);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);

            FluidStack fluidStack = tile.getFluidInventory().getFluid();
            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStack.getFluid().getAttributes().getStillTexture(fluidStack));

            int fluidColor = Fluids.WATER.getAttributes().getColor(tile.getWorld(), tile.getPos());

            IVertexBuilder vertex = buffer.getBuffer(Atlases.getTranslucentBlockType());
            RenderHelper.renderIconColored(matrixStack, vertex, 0, 0, sprite, 8, 8, 1.0F, fluidColor, light, OverlayTexture.NO_OVERLAY);

            matrixStack.pop();
        }

        int items = 0;
        for (int slot : tile.getInventory().getInputSlots()) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty())
                items += 1;
        }

        double offsetPerPetal = 360d / items;
        double flowerTicks = (double) ((float) ClientTickHandler.ticksInGame + partialTicks) / 2;

        matrixStack.push();
        matrixStack.translate(0.5, (10 + ((fluidAmount * 3.8) / 1.5)) / 16, 0.5);
        matrixStack.scale(0.125f, 0.125f, 0.125f);

        int nextIdx = 0;
        boolean hasFluid = tile.getFluidInventory().getFluidAmount() > 0;

        for (int slot : tile.getInventory().getInputSlots()) {
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

                matrixStack.push();
                matrixStack.translate(x, y, z);

                matrixStack.translate(0.0625f, 0.0625f, 0.0625f);
                if (hasFluid) {
                    float xRotate = (float) Math.sin(flowerTicks * 0.25) / 2;
                    float yRotate = (float) Math.max(0.6000000238418579, Math.sin(flowerTicks * 0.10000000149011612) / 2 + 0.5);
                    float zRotate = (float) Math.cos(flowerTicks * 0.25) / 2;
                    matrixStack.rotate((new Vector3f(xRotate, yRotate, zRotate)).rotationDegrees((float) deg));
                } else {
                    matrixStack.rotate((Vector3f.XP.rotationDegrees(90)));
                }
                matrixStack.translate(-0.0625f, -0.0625f, -0.0625f);

                ItemStack stack = tile.getInventory().getStackInSlot(slot);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, overlay, matrixStack, buffer);
                matrixStack.pop();
            }
        }
        matrixStack.pop();
    }
}
