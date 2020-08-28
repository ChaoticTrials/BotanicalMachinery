package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
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

public class BlockAlfheimMarket extends BlockBase {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BlockBase.FRAME_SHAPE,
            makeCuboidShape(4.4, 1, 8.8, 11.6, 13, 11.2),
            makeCuboidShape(0, 0, 8.8, 0, 0, 11.2),
            makeCuboidShape(3.2, 0, 3.6, 6.8, 7.4, 7.2),
            makeCuboidShape(8.8, 0, 3.6, 12.4, 7.4, 7.2)
    ));

    public BlockAlfheimMarket() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new TileAlfheimMarket();
    }

    @Nullable
    @Override
    protected ContainerType<?> getContainerType() {
        return Registration.CONTAINER_ALFHEIM_MARKET.get();
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
