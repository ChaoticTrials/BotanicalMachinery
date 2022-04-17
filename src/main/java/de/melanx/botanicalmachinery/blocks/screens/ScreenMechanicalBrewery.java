package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalBrewery;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ScreenMechanicalBrewery extends ScreenBase<ContainerMenuMechanicalBrewery> {

    public ScreenMechanicalBrewery(ContainerMenuMechanicalBrewery menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 192;
        this.manaBar.y = 28;
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(poseStack, LibResources.MECHANICAL_BREWERY_GUI);

        BlockEntityMechanicalBrewery blockEntity = this.menu.getBlockEntity();
        if (blockEntity.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            List<Item> items = BlockEntityMechanicalBrewery.BREW_CONTAINER;
            int idx = Math.abs(ClientTickHandler.ticksInGame / 20) % items.size();
            // TODO semi transparent items preview?
//            RenderHelperItem.renderItemGui(poseStack, this.minecraft.renderBuffers().bufferSource(), new ItemStack(items.get(idx)), this.relX + 44, this.relY + 48, 16, false, 1, 1, 1, 0.3f);
        }

        if (blockEntity.getProgress() > 0) {
            float pct = Math.min(blockEntity.getProgress() / (float) blockEntity.getMaxProgress(), 1.0F);
            RenderSystem.setShaderTexture(0, LibResources.MECHANICAL_BREWERY_GUI);
            vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(poseStack, this.relX + 96, this.relY + 48, 176, 0, Math.round(22 * pct), 16);
        }
    }
}
