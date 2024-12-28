package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalBreweryRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalBrewery;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

public class BlockMechanicalBrewery extends BotanicalBlock<BlockEntityMechanicalBrewery, ContainerMenuMechanicalBrewery> {

    public BlockMechanicalBrewery(ModX mod, Class<BlockEntityMechanicalBrewery> teClass, MenuType<ContainerMenuMechanicalBrewery> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.mechanicalBrewery.menu, ScreenMechanicalBrewery::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalBreweryRenderer());
    }
}
