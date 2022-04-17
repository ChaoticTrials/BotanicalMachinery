package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.screens.ScreenIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tesr.IndustrialAgglomerationFactoryRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityIndustrialAgglomerationFactory;
import io.github.noeppi_noeppi.libx.block.RotationShape;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockIndustrialAgglomerationFactory extends BotanicalBlock<BlockEntityIndustrialAgglomerationFactory, ContainerMenuIndustrialAgglomerationFactory> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(2.6, 0, 2.6, 13.4, 4.6, 13.4),
            box(6.2, 0, 6.2, 9.8, 5.3, 9.8)
    ));

    public BlockIndustrialAgglomerationFactory(ModX mod, Class<BlockEntityIndustrialAgglomerationFactory> teClass, MenuType<ContainerMenuIndustrialAgglomerationFactory> menu) {
        super(mod, teClass, menu, false, true);
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        super.registerClient(id, defer);
        MenuScreens.register(ModBlocks.industrialAgglomerationFactory.menu, ScreenIndustrialAgglomerationFactory::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new IndustrialAgglomerationFactoryRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
