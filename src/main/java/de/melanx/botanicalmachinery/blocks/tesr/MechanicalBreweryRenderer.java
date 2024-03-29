package de.melanx.botanicalmachinery.blocks.tesr;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalBrewery;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Quaternionf;
import org.moddingx.libx.render.RenderHelper;
import org.moddingx.libx.render.block.RotatedBlockRenderer;
import vazkii.botania.api.brew.BrewItem;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.item.BotaniaItems;

import javax.annotation.Nonnull;
import java.util.Map;

public class MechanicalBreweryRenderer extends RotatedBlockRenderer<BlockEntityMechanicalBrewery> {

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
            .put(BotaniaItems.manaSteel, 0x006bff)
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

    public MechanicalBreweryRenderer() {
        this.waterColor = IClientFluidTypeExtensions.of(Fluids.WATER).getTintColor();
    }

    @Override
    protected void doRender(@Nonnull BlockEntityMechanicalBrewery tile, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!LibXClientConfig.AdvancedRendering.all || !LibXClientConfig.AdvancedRendering.mechanicalBrewery)
            return;

        int slotToMove = -1;
        double travelCenter = 1;
        Quaternionf vialRotate = null;
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
                vialRotate = Axis.XP.rotationDegrees((float) (480 * vialDown));
                vialDown = 1.8 * vialDown;
                showOutput = progressMinusHalf >= 0;
                this.renderFluid(poseStack, buffer, partialTick, light, (float) (1 - segmentProgress), this.getTargetColor(tile));
            } else if (progress >= 1 - (2 * segment)) {
                slotToMove = Integer.MAX_VALUE;

                int fromColor = this.waterColor;
                for (int i = 6; i >= 1; i--) {
                    if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                        fromColor = this.getColor(tile.getInventory().getStackInSlot(i));
                        break;
                    }
                }
                this.renderFluid(poseStack, buffer, partialTick, light, 1, fromColor, this.getTargetColor(tile), segmentProgress);
            } else if (progress < segment) {
                this.renderFluid(poseStack, buffer, partialTick, light, (float) segmentProgress, this.waterColor);
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

                this.renderFluid(poseStack, buffer, partialTick, light, 1, fromColor, toColor, segmentProgress);
            }
        }

        ItemStack topStack = tile.getInventory().getStackInSlot(7);
        if (showOutput && !tile.getCurrentOutput().isEmpty()) {
            topStack = tile.getCurrentOutput();
        } else if (topStack.isEmpty() || tile.getProgress() > 0) {
            topStack = tile.getInventory().getStackInSlot(0);
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.8 + vialDown, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees((ClientTickHandler.ticksInGame + partialTick) / 1.3f));
        if (vialRotate != null)
            poseStack.mulPose(vialRotate);

        Minecraft.getInstance().getItemRenderer().renderStatic(topStack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, tile.getLevel(), (int) tile.getBlockPos().asLong());

        poseStack.popPose();

        int itemAmount = 0;
        for (int i = 1; i <= 6; i++) {
            if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                itemAmount += 1;
            }
        }

        double angle = 360d / itemAmount;
        float time = ClientTickHandler.ticksInGame + partialTick;

        int idx = 0;
        for (int i = 1; i <= 6; i++) {
            if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                int idxNow = idx++;
                if (i < slotToMove)
                    continue;
                poseStack.pushPose();
                poseStack.translate(0.5, 0.7, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.YP.rotationDegrees((float) -((angle * idxNow) + time)));
                if (i == slotToMove) {
                    poseStack.translate((1 - travelCenter) * 1.125, travelCenter * -1, (1 - travelCenter) * 0.25);
                    poseStack.mulPose(Axis.XP.rotationDegrees((float) (90 * travelCenter)));
                } else {
                    poseStack.translate(1.125, 0, 0.25);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                poseStack.translate(0, 0.075 * Math.sin((time + (idxNow * 10)) / 5d), 0);

                Minecraft.getInstance().getItemRenderer().renderStatic(tile.getInventory().getStackInSlot(i), ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, tile.getLevel(), (int) tile.getBlockPos().asLong());

                poseStack.popPose();
            }
        }
    }

    private int getColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return this.waterColor;
        } else if (INGREDIENT_COLORS.containsKey(stack.getItem())) {
            return INGREDIENT_COLORS.get(stack.getItem());
        } else if (stack.getItem() instanceof DyeItem) {
            return ((DyeItem) stack.getItem()).getDyeColor().getTextColor();
        } else if (stack.getItem() instanceof BlockItem) {
            return ((BlockItem) stack.getItem()).getBlock().defaultMapColor().col;
        } else {
            return this.waterColor;
        }
    }

    private int getTargetColor(BlockEntityMechanicalBrewery tile) {
        if (tile.getCurrentOutput().getItem() instanceof BrewItem brewItem) {
            return brewItem.getBrew(tile.getCurrentOutput()).getColor(tile.getCurrentOutput());
        } else {
            return this.waterColor;
        }
    }

    private void renderFluid(PoseStack poseStack, @Nonnull MultiBufferSource buffer, float partialTick, int light, @SuppressWarnings("SameParameterValue") float fillLevel, int colorFrom, int colorTo, double progress) {
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

        this.renderFluid(poseStack, buffer, partialTick, light, fillLevel, color);
    }

    private void renderFluid(PoseStack poseStack, @Nonnull MultiBufferSource buffer, float partialTick, int light, float fillLevel, int color) {
        poseStack.pushPose();
        poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        poseStack.translate(4, 3 + (4.4 * fillLevel), 4);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture());

        VertexConsumer vertex = buffer.getBuffer(Sheets.translucentCullBlockSheet());
        RenderHelper.renderIconColored(poseStack, vertex, 0, 0, sprite, 8, 8, 1.0F, color, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
