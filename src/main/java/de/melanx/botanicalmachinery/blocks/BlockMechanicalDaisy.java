package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalDaisy;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockMechanicalDaisy extends BlockGUI<TileMechanicalDaisy, ContainerMechanicalDaisy> {

    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.combine(
            makeCuboidShape(0, 0, 0, 16, 2, 16),
            makeCuboidShape(5, 2, 5, 11, 3, 11),
            IBooleanFunction.OR
    );

    private static final VoxelShape SHAPE = makeCuboidShape(0, 0, 0, 16, 11.4, 16);

    public BlockMechanicalDaisy(ModX mod, Class<TileMechanicalDaisy> teClass, ContainerType<ContainerMechanicalDaisy> container) {
        super(mod, teClass, container, Properties.create(Material.ROCK).hardnessAndResistance(2, 10).variableOpacity(),
                new Item.Properties().setISTER(() -> ItemStackRenderer::get));
    }

    @Override
    public void registerClient(ResourceLocation id) {
        RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
        ItemStackRenderer.addRenderTile(this.getTileType(), true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getOpacity(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return COLLISION_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getRenderShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected boolean shouldDropInventory(World world, BlockPos pos, BlockState state) {
        return false;
    }
}
