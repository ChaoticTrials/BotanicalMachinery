package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.screens.ScreenAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tesr.AlfheimMarketRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityAlfheimMarket;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

public class BlockAlfheimMarket extends BotanicalBlock<BlockEntityAlfheimMarket, ContainerMenuAlfheimMarket> {

    public BlockAlfheimMarket(ModX mod, Class<BlockEntityAlfheimMarket> teClass, MenuType<ContainerMenuAlfheimMarket> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.alfheimMarket.menu, ScreenAlfheimMarket::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new AlfheimMarketRenderer());
    }
}
