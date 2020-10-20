package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
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

public class BlockMechanicalRunicAltar extends BotanicalBlock<TileMechanicalRunicAltar, ContainerMechanicalRunicAltar> {

    public static final DirectionShape SHAPE = new DirectionShape(VoxelShapes.or(
            BotanicalBlock.FRAME_SHAPE,
            makeCuboidShape(2, 5, 2, 14, 9, 14),
            makeCuboidShape(6, 3, 6, 10, 5, 10),
            makeCuboidShape(4, 1, 4, 12, 3, 12)
    ));

    public BlockMechanicalRunicAltar(ModX mod, Class<TileMechanicalRunicAltar> teClass, ContainerType<ContainerMechanicalRunicAltar> container) {
        super(mod, teClass, container, false);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
