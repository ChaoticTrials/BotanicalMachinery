package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMenuMechanicalManaPool> {

    public ScreenMechanicalManaPool(ContainerMenuMechanicalManaPool menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(poseStack, LibResources.MECHANICAL_MANA_POOL_GUI);

        BlockEntityMechanicalManaPool blockEntity = this.menu.getBlockEntity();
        if (blockEntity.getInventory().getStackInSlot(0).isEmpty() && this.minecraft != null) {
            List<Item> items = BlockEntityMechanicalManaPool.CATALYSTS;
            int idx = Math.abs(ClientTickHandler.ticksInGame / 20) % items.size();
            // TODO semi transparent items preview?
//            RenderHelperItem.renderItemGui(poseStack, this.minecraft.renderBuffers().bufferSource(), new ItemStack(items.get(idx)), this.relX + 53, this.relY + 47, 16, false, 1, 1, 1, 0.3f);
        }
    }
}
