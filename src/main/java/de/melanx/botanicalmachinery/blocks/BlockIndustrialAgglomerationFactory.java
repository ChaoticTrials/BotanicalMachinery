package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.screens.ScreenIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tesr.IndustrialAgglomerationFactoryRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityIndustrialAgglomerationFactory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.block.RotationShape;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;

public class BlockIndustrialAgglomerationFactory extends BotanicalBlock<BlockEntityIndustrialAgglomerationFactory, ContainerMenuIndustrialAgglomerationFactory> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(2.6, 0, 2.6, 13.4, 4.6, 13.4),
            box(6.2, 0, 6.2, 9.8, 5.3, 9.8)
    ));

    public BlockIndustrialAgglomerationFactory(ModX mod, Class<BlockEntityIndustrialAgglomerationFactory> teClass, MenuType<ContainerMenuIndustrialAgglomerationFactory> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.industrialAgglomerationFactory.menu, ScreenIndustrialAgglomerationFactory::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new IndustrialAgglomerationFactoryRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
