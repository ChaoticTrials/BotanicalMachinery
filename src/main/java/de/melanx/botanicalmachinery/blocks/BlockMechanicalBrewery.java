package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
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

public class BlockMechanicalBrewery extends BotanicalBlock<TileMechanicalBrewery, ContainerMechanicalBrewery> {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BotanicalBlock.FRAME_SHAPE,
            makeCuboidShape(5, 1, 5, 6, 2, 6),
            makeCuboidShape(5, 1, 10, 6, 2, 11),
            makeCuboidShape(10, 1, 5, 11, 2, 6),
            makeCuboidShape(10, 1, 10, 11, 2, 11),
            makeCuboidShape(3, 2, 3, 13, 3, 13),
            makeCuboidShape(3, 3, 12, 13, 8, 13),
            makeCuboidShape(3, 3, 3, 13, 8, 4),
            makeCuboidShape(12, 3, 4, 13, 8, 12),
            makeCuboidShape(3, 3, 4, 4, 8, 12)
    ));

    public BlockMechanicalBrewery(ModX mod, Class<TileMechanicalBrewery> teClass, ContainerType<ContainerMechanicalBrewery> container) {
        super(mod, teClass, container, false);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
