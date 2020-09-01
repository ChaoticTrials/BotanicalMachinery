package de.melanx.botanicalmachinery.blocks.tesr;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.*;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.item.Item16Colors;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.Map;

public class TesrMechanicalBrewery extends HorizontalRotatedTesr<TileMechanicalBrewery> {

    public static final Map<Item, Integer> INGREDIENT_COLORS = ImmutableMap.<Item, Integer>builder()
            .put(Items.NETHER_WART, 0xbe3f4a)
            .put(Items.PRISMARINE_CRYSTALS, 0x91c5b7)
            .put(Items.GLOWSTONE_DUST, 0xffbc5e)
            .put(Items.GOLDEN_APPLE, 0xdba213)
            .put(Items.POTATO, 0xe9ba62)
            .put(Items.SUGAR, 0xd5d5df)
            .put(Items.GOLD_NUGGET, 0xf9f969)
            .put(Items.IRON_INGOT, 0xd8d8d8)
            .put(Items.LEATHER, 0xc65c35)
            .put(Items.MAGMA_CREAM, 0xd58520)
            .put(Items.FERMENTED_SPIDER_EYE, 0x65062b)
            .put(Items.LAPIS_LAZULI, 0x345ec3)
            .put(Items.FIRE_CHARGE, 0xeeac18)
            .put(Items.GLISTERING_MELON_SLICE, 0xc94908)
            .put(Items.GHAST_TEAR, 0x9fc3c3)
            .put(Items.GUNPOWDER, 0x727272)
            .put(Items.ROTTEN_FLESH, 0x834418)
            .put(Items.BONE, 0xfcfbed)
            .put(Items.STRING, 0xdbdbdb)
            .put(Items.ENDER_PEARL, 0x349988)
            .put(Items.BLAZE_POWDER, 0xffe000)
            .put(ModItems.manaSteel, 0x006bff)
            .put(Items.SPIDER_EYE, 0x9d1e2d)
            .put(Items.GOLDEN_CARROT, 0xdba213)
            .put(Items.PAPER, 0xe9eaeb)
            .put(Items.APPLE, 0xdd1725)
            .put(Items.FEATHER, 0x969696)
            .put(Items.CARROT, 0xff8e09)
            .put(Items.REDSTONE, 0xea0400)
            .put(Items.COD, 0xc6a271)
            .put(Items.QUARTZ, 0xddd4c6)
            .put(Items.SNOWBALL, 0xffffff)
            .put(Items.EMERALD, 0x17dd62)
            .put(Items.MELON_SLICE, 0xbf3123)
            .build();

    private final int waterColor;

    public TesrMechanicalBrewery(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.waterColor = Fluids.WATER.getAttributes().getColor();
    }

    @Override
    protected void doRender(@Nonnull TileMechanicalBrewery tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (!ClientConfig.everything.get() || !ClientConfig.brewery.get())
            return;

        int slotToMove = -1;
        double travelCenter = 1;
        Quaternion vialRotate = null;
        double vialDown = 0;
        boolean showOutput = false;

        if (tile.getProgress() > 0) {
            double progress = tile.getProgress() / (double) tile.getMaxProgress();

            int segments = 3;
            for (int i = 1; i <= 6; i++) {
                if (!tile.getInventory().getStackInSlot(i).isEmpty())
                    segments += 1;
            }

            double segment = 1d / segments;
            double segmentProgress = (progress % segment) * segments;

            if (progress >= 1 - segment) {
                slotToMove = Integer.MAX_VALUE;

                double progressMinusHalf = segmentProgress - 0.5;
                vialDown = (progressMinusHalf * progressMinusHalf) - 0.25;
                vialRotate = Vector3f.XP.rotationDegrees((float) (480 * vialDown));
                vialDown = 1.8 * vialDown;
                showOutput = progressMinusHalf >= 0;
                this.renderFluid(matrixStack, buffer, partialTicks, light, (float) (1 - segmentProgress), this.getTargetColor(tile));
            } else if (progress >= 1 - (2 * segment)) {
                slotToMove = Integer.MAX_VALUE;

                int fromColor = this.waterColor;
                for (int i = 6; i >= 1; i--) {
                    if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                        fromColor = this.getColor(tile.getInventory().getStackInSlot(i));
                        break;
                    }
                }
                this.renderFluid(matrixStack, buffer, partialTicks, light, 1, fromColor, this.getTargetColor(tile), segmentProgress);
            } else if (progress < segment) {
                this.renderFluid(matrixStack, buffer, partialTicks, light, (float) segmentProgress, this.waterColor);
            } else {
                int idx = (int) ((progress - segment) / segment);

                int fromColor = this.waterColor;
                int toColor = this.waterColor;

                for (int i = 1; i <= 6; i++) {
                    if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                        if (idx <= 0) {
                            slotToMove = i;
                            travelCenter = segmentProgress;
                            toColor = this.getColor(tile.getInventory().getStackInSlot(i));
                            break;
                        } else {
                            if (idx == 1) {
                                fromColor = this.getColor(tile.getInventory().getStackInSlot(i));
                            }
                            idx -= 1;
                        }
                    }
                }

                this.renderFluid(matrixStack, buffer, partialTicks, light, 1, fromColor, toColor, segmentProgress);
            }
        }

        ItemStack topStack = tile.getInventory().getStackInSlot(7);
        if (showOutput && !tile.getCurrentOutput().isEmpty()) {
            topStack = tile.getCurrentOutput();
        } else if (topStack.isEmpty() || tile.getProgress() > 0) {
            topStack = tile.getInventory().getStackInSlot(0);
        }

        matrixStack.push();
        matrixStack.translate(0.5, 0.8 + vialDown, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((ClientTickHandler.ticksInGame + partialTicks) / 1.3f));
        if (vialRotate != null)
            matrixStack.rotate(vialRotate);

        Minecraft.getInstance().getItemRenderer().renderItem(topStack, ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

        matrixStack.pop();

        int itemAmount = 0;
        for (int i = 1; i <= 6; i++) {
            if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                itemAmount += 1;
            }
        }

        double angle = 360d / itemAmount;
        float time = ClientTickHandler.ticksInGame + partialTicks;

        int idx = 0;
        for (int i = 1; i <= 6; i++) {
            if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                int idxNow = idx++;
                if (i < slotToMove)
                    continue;
                matrixStack.push();
                matrixStack.translate(0.5, 0.7, 0.5);
                matrixStack.scale(0.3f, 0.3f, 0.3f);
                matrixStack.rotate(Vector3f.YP.rotationDegrees((float) -((angle * idxNow) + time)));
                if (i == slotToMove) {
                    matrixStack.translate((1 - travelCenter) * 1.125, travelCenter * -1, (1 - travelCenter) * 0.25);
                    matrixStack.rotate(Vector3f.XP.rotationDegrees((float) (90 * travelCenter)));
                } else {
                    matrixStack.translate(1.125, 0, 0.25);
                }
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90f));
                matrixStack.translate(0, 0.075 * Math.sin((time + (idxNow * 10)) / 5d), 0);

                Minecraft.getInstance().getItemRenderer().renderItem(tile.getInventory().getStackInSlot(i), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

                matrixStack.pop();
            }
        }
    }

    private int getColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return this.waterColor;
        } else if (INGREDIENT_COLORS.containsKey(stack.getItem())) {
            return INGREDIENT_COLORS.get(stack.getItem());
        } else if (stack.getItem() instanceof DyeItem) {
            return ((DyeItem) stack.getItem()).getDyeColor().getColorValue();
        } else if (stack.getItem() instanceof BlockItem) {
            //noinspection deprecation
            return ((BlockItem) stack.getItem()).getBlock().getMaterial(((BlockItem) stack.getItem()).getBlock().getDefaultState()).getColor().colorValue;
        } else if (stack.getItem() instanceof Item16Colors) {
            return ((Item16Colors) stack.getItem()).color.getColorValue();
        } else {
            return this.waterColor;
        }
    }

    private int getTargetColor(TileMechanicalBrewery tile) {
        if (tile.getCurrentOutput().getItem() instanceof IBrewItem) {
            return ((IBrewItem) tile.getCurrentOutput().getItem()).getBrew(tile.getCurrentOutput()).getColor(tile.getCurrentOutput());
        } else {
            return this.waterColor;
        }
    }

    private void renderFluid(MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, float partialTicks, int light, float fillLevel, int colorFrom, int colorTo, double progress) {
        int fromRed = colorFrom >> 16 & 255;
        int fromGreen = colorFrom >> 8 & 255;
        int fromBlue = colorFrom & 255;

        int toRed = colorTo >> 16 & 255;
        int toGreen = colorTo >> 8 & 255;
        int toBlue = colorTo & 255;

        int red = (int) Math.round(fromRed + ((toRed - fromRed) * progress));
        int green = (int) Math.round(fromGreen + ((toGreen - fromGreen) * progress));
        int blue = (int) Math.round(fromBlue + ((toBlue - fromBlue) * progress));

        int color = (red << 16) | (green << 8) | blue;

        this.renderFluid(matrixStack, buffer, partialTicks, light, fillLevel, color);
    }

    private void renderFluid(MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, float partialTicks, int light, float fillLevel, int color) {
        matrixStack.push();
        matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        matrixStack.translate(4, 3 + (4.4 * fillLevel), 4);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90));

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(Fluids.WATER.getAttributes().getStillTexture());

        IVertexBuilder vertex = buffer.getBuffer(Atlases.getTranslucentBlockType());
        RenderHelper.renderIconColored(matrixStack, vertex, 0, 0, sprite, 8, 8, 1.0F, color, light, OverlayTexture.NO_OVERLAY);

        matrixStack.pop();
    }
}
