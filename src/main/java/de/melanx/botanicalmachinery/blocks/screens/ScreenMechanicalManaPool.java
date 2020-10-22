package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMechanicalManaPool> {

    public ScreenMechanicalManaPool(ContainerMechanicalManaPool container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(ms, LibResources.MECHANICAL_MANA_POOL_GUI, 81, 37);

        TileMechanicalManaPool tile = (TileMechanicalManaPool) this.container.tile;
        if (tile.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            List<Item> items = TileMechanicalManaPool.CATALYSTS;
            int idx = Math.abs(ClientTickHandler.ticksInGame / 20) % items.size();
            RenderHelperItem.renderItemGui(ms, this.minecraft.getRenderTypeBuffers().getBufferSource(), new ItemStack(items.get(idx)), this.relX + 53, this.relY + 47, 16, false, 1, 1, 1, 0.3f);
        }
    }
}
