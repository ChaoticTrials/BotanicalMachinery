package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import io.github.noeppi_noeppi.libx.block.DirectionShape;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockAlfheimMarket extends BotanicalBlock<TileAlfheimMarket, ContainerAlfheimMarket> {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BotanicalBlock.FRAME_SHAPE,
            makeCuboidShape(4.4, 1, 8.8, 11.6, 13, 11.2),
            makeCuboidShape(0, 0, 8.8, 0, 0, 11.2),
            makeCuboidShape(3.2, 0, 3.6, 6.8, 7.4, 7.2),
            makeCuboidShape(8.8, 0, 3.6, 12.4, 7.4, 7.2)
    ));

    public BlockAlfheimMarket(ModX mod, Class<TileAlfheimMarket> teClass, ContainerType<ContainerAlfheimMarket> container) {
        super(mod, teClass, container, false, true);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
