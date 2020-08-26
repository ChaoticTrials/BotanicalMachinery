package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
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

public class BlockMechanicalManaPool extends BlockBase {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BlockBase.FRAME_SHAPE,
            makeCuboidShape(2, 1, 2, 14, 1.1, 14),
            makeCuboidShape(2, 1, 13, 14, 6, 14),
            makeCuboidShape(2, 1, 2, 14, 6, 3),
            makeCuboidShape(13, 1, 3, 14, 6, 13),
            makeCuboidShape(2, 1, 3, 3, 6, 13)
    ));

    public BlockMechanicalManaPool() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new TileMechanicalManaPool();
    }

    @Nullable
    @Override
    protected ContainerType<?> getContainerType() {
        return Registration.CONTAINER_MECHANICAL_MANA_POOL.get();
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
