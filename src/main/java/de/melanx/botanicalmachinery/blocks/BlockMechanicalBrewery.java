package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.util.DirectionShape;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMechanicalBrewery extends BlockBase {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BlockBase.FRAME_SHAPE,
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

    public BlockMechanicalBrewery() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new TileMechanicalBrewery();
    }

    @Nullable
    @Override
    protected ContainerType<?> getContainerType() {
        return Registration.CONTAINER_MECHANICAL_BREWERY.get();
    }

    @Nonnull
    @Override
    public VoxelShape getRenderShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
