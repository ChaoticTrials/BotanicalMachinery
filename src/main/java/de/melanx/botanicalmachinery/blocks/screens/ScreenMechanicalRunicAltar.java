package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.core.LibResources;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.common.block.ModBlocks;

import javax.annotation.Nonnull;

public class ScreenMechanicalRunicAltar extends ScreenBase<ContainerMechanicalRunicAltar> {

    private final ItemStack livingRock;

    public ScreenMechanicalRunicAltar(ContainerMechanicalRunicAltar container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.xSize = 216;
        this.ySize = 195;
        this.manaBar.x += 40;
        this.manaBar.y += 20;

        this.livingRock = new ItemStack(ModBlocks.livingrock);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(ms, LibResources.MECHANICAL_RUNIC_ALTAR_GUI, 91, 65);

        TileMechanicalRunicAltar tile = (TileMechanicalRunicAltar) this.container.tile;
        if (tile.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            RenderHelperItem.renderItemGui(ms, this.minecraft.getRenderTypeBuffers().getBufferSource(), this.livingRock, this.relX + 90, this.relY + 43, 16, false, 1, 1, 1, 0.3f);
        }
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / (float) tile.getMaxProgress(), 1.0F);
            //noinspection ConstantConditions
            this.minecraft.getTextureManager().bindTexture(LibResources.MECHANICAL_RUNIC_ALTAR_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(ms, this.relX + 87, this.relY + 64, this.xSize, 0, Math.round(22 * pct), 16);
        }
    }
}
