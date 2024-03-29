package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalApothecary;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.GhostItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.Tags;
import org.moddingx.libx.render.RenderHelperFluid;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.List;

public class ScreenMechanicalApothecary extends AbstractContainerScreen<ContainerMenuMechanicalApothecary> {

    private int relX;
    private int relY;
    private final BlockEntityMechanicalApothecary tile;

    public ScreenMechanicalApothecary(ContainerMenuMechanicalApothecary screenMenu, Inventory inventory, Component title) {
        super(screenMenu, inventory, title);
        this.imageWidth = 196;
        this.imageHeight = 195;
        this.relX = (this.width - this.imageWidth) / 2;
        this.relY = (this.height - this.imageHeight) / 2;
        this.tile = (BlockEntityMechanicalApothecary) this.menu.getLevel().getBlockEntity(this.menu.getPos());
    }

    @Override
    public void init(@Nonnull Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        this.relX = (width - this.imageWidth) / 2;
        this.relY = (height - this.imageHeight) / 2;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(LibResources.MECHANICAL_APOTHECARY_GUI, this.relX, this.relY, 0, 0, this.imageWidth, this.imageHeight);

        if (this.tile.getInventory().getStackInSlot(0).isEmpty()) {
            //noinspection DataFlowIssue,OptionalGetWithoutIsPresent
            List<ItemStack> items = this.minecraft.level.registryAccess().registry(Registries.ITEM).get().getOrCreateTag(Tags.Items.SEEDS).stream().map(Holder::value).map(ItemStack::new).toList();
            GhostItemRenderer.renderGhostItem(items, guiGraphics, this.relX + 90, this.relY + 43);
        }

        if (this.tile.getProgress() > 0) {
            float pctProgress = Math.min(this.tile.getProgress() / (float) this.tile.getMaxProgress(), 1.0F);
            RenderSystem.setShaderTexture(0, LibResources.MECHANICAL_APOTHECARY_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(guiGraphics, LibResources.MECHANICAL_APOTHECARY_GUI, this.relX + 87, this.relY + 64, this.imageWidth, 0, Math.round(22 * pctProgress), 16);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String s = this.title.getString();
        guiGraphics.drawString(this.font, s, (float) (this.imageWidth / 2 - this.font.width(s) / 2), 6.0F, Color.DARK_GRAY.getRGB(), false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle.getString(), 8.0F, (float) (this.imageHeight - 96 + 2), Color.DARK_GRAY.getRGB(), false);

        float pctFluid = Math.min((float) this.tile.getFluidInventory().getFluidAmount() / BlockEntityMechanicalApothecary.FLUID_CAPACITY, 1.0F);
        int xPos = 163;
        int ySize = Math.round(81 * pctFluid);
        int yPos = 16 + 81 - ySize;

        RenderHelperFluid.renderFluid(guiGraphics, IClientFluidTypeExtensions.of(Fluids.WATER).getTintColor(), xPos, yPos, 17, ySize);

        guiGraphics.blit(LibResources.MECHANICAL_APOTHECARY_GUI, xPos, 16, this.imageWidth, 16, 17, 81);

        this.renderTooltip(guiGraphics, mouseX - this.leftPos, mouseY - this.topPos);
    }

    @Override
    protected void renderTooltip(@Nonnull GuiGraphics guiGraphics, int x, int y) {
        if (x >= 163 && x <= 179 &&
                y >= 16 && y <= 96) {
            Component fluid = Component.translatable(this.tile.getFluidInventory().getFluidAmount() + " / " + this.tile.getFluidInventory().getCapacity() + " mB");
            guiGraphics.renderTooltip(this.font, fluid, x, y);
        }
        super.renderTooltip(guiGraphics, x, y);
    }
}
