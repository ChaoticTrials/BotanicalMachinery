package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BaseBlock;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.core.Registration;
import io.github.noeppi_noeppi.libx.block.DirectionShape;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockIndustrialAgglomerationFactory extends BaseBlock {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BaseBlock.FRAME_SHAPE,
            makeCuboidShape(2.6, 0, 2.6, 13.4, 4.6, 13.4),
            makeCuboidShape(6.2, 0, 6.2, 9.8, 5.3, 9.8)
    ));

    public BlockIndustrialAgglomerationFactory() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new TileIndustrialAgglomerationFactory();
    }

    @Nullable
    @Override
    protected ContainerType<?> getContainerType() {
        return Registration.CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY.get();
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

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull BlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileIndustrialAgglomerationFactory tile = (TileIndustrialAgglomerationFactory) worldIn.getTileEntity(pos);
        return tile != null && tile.getProgress() > 0 ? 15 : 0;
    }
}
