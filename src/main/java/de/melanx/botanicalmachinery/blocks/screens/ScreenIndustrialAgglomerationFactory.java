package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.core.LibResources;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

public class ScreenIndustrialAgglomerationFactory extends ScreenBase<ContainerIndustrialAgglomerationFactory> {

    private final ItemStack manaSteel;
    private final ItemStack manaDiamond;
    private final ItemStack manaPearl;

    public ScreenIndustrialAgglomerationFactory(ContainerIndustrialAgglomerationFactory container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.ySize = 195;
        this.manaBar.x -= 5;
        this.manaBar.y += 23;

        this.manaSteel = new ItemStack(ModItems.manaSteel);
        this.manaDiamond = new ItemStack(ModItems.manaDiamond);
        this.manaPearl = new ItemStack(ModItems.manaPearl);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(ms, LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI, 81, 37);
        if (this.minecraft != null) {
            RenderHelperItem.renderItemGui(ms, this.minecraft.getRenderTypeBuffers().getBufferSource(), this.manaSteel, this.relX + 61, this.relY + 83, 16, false, 1, 1, 1, 0.5f);
            RenderHelperItem.renderItemGui(ms, this.minecraft.getRenderTypeBuffers().getBufferSource(), this.manaDiamond, this.relX + 80, this.relY + 83, 16, false, 1, 1, 1, 0.5f);
            RenderHelperItem.renderItemGui(ms, this.minecraft.getRenderTypeBuffers().getBufferSource(), this.manaPearl, this.relX + 99, this.relY + 83, 16, false, 1, 1, 1, 0.5f);
        }
        TileIndustrialAgglomerationFactory tile = (TileIndustrialAgglomerationFactory) this.container.tile;
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / (float) tile.getMaxProgress(), 1.0F);
            this.minecraft.getTextureManager().bindTexture(LibResources.INDUSTRIAL_AGGLOMERATION_FACTORY_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(ms, this.relX + 73, this.relY + 76, 176, 25, 30, Math.round(-(25 * pct)));
        }
    }
}
