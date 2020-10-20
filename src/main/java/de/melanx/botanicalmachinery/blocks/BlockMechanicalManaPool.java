package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import io.github.noeppi_noeppi.libx.block.DirectionShape;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
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

public class BlockMechanicalManaPool extends BotanicalBlock<TileMechanicalManaPool, ContainerMechanicalManaPool> {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BotanicalBlock.FRAME_SHAPE,
            makeCuboidShape(2, 1, 2, 14, 1.1, 14),
            makeCuboidShape(2, 1, 13, 14, 6, 14),
            makeCuboidShape(2, 1, 2, 14, 6, 3),
            makeCuboidShape(13, 1, 3, 14, 6, 13),
            makeCuboidShape(2, 1, 3, 3, 6, 13)
    ));

    public BlockMechanicalManaPool(ModX mod, Class<TileMechanicalManaPool> teClass, ContainerType<ContainerMechanicalManaPool> container) {
        super(mod, teClass, container, false);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
