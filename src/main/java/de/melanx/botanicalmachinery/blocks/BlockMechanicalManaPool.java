package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalManaPoolRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

public class BlockMechanicalManaPool extends BotanicalBlock<BlockEntityMechanicalManaPool, ContainerMenuMechanicalManaPool> {

    public BlockMechanicalManaPool(ModX mod, Class<BlockEntityMechanicalManaPool> teClass, MenuType<ContainerMenuMechanicalManaPool> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.mechanicalManaPool.menu, ScreenMechanicalManaPool::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalManaPoolRenderer());
    }
}
