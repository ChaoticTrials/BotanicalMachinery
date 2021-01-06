package de.melanx.botanicalmachinery.blocks.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandHUD;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BotanicalBlock<T extends BotanicalTile, C extends ContainerBase<T>> extends BlockGUI<T, C> implements IWandHUD {

    public static final VoxelShape FRAME_SHAPE = VoxelShapes.or(
            makeCuboidShape(0, 0, 0, 16, 1, 16),
            makeCuboidShape(0, 0, 0, 1, 16, 1),
            makeCuboidShape(15, 0, 0, 16, 16, 1),
            makeCuboidShape(0, 0, 15, 1, 16, 16),
            makeCuboidShape(15, 0, 15, 16, 16, 16),
            makeCuboidShape(0, 15, 0, 1, 16, 16),
            makeCuboidShape(0, 15, 0, 16, 16, 1),
            makeCuboidShape(15, 15, 0, 16, 16, 16),
            makeCuboidShape(0, 15, 15, 16, 16, 16)
    );

    public final boolean fullCube;
    public final boolean specialRender;

    public BotanicalBlock(ModX mod, Class<T> teClass, ContainerType<C> container, boolean fullCube, boolean specialRender) {
        super(mod, teClass, container, fullCube ?
                Properties.create(Material.ROCK).hardnessAndResistance(2, 10)
                : Properties.create(Material.ROCK).hardnessAndResistance(2, 10).variableOpacity(),
                specialRender ? new Item.Properties().setISTER(() -> ItemStackRenderer::get) : new Item.Properties());
        this.fullCube = fullCube;
        this.specialRender = specialRender;
    }

    @Override
    public void registerClient(ResourceLocation id) {
        if (!this.fullCube) {
            RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
        }
        if (this.specialRender) {
            ItemStackRenderer.addRenderTile(this.getTileType(), true);
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        BotanicalTile tile = (BotanicalTile) world.getTileEntity(pos);
        if (tile == null) return null;
        CompoundNBT nbt = tile.serializeNBT();
        ItemStack stack = new ItemStack(this);
        stack.setTag(nbt);
        return stack;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return this.getTile(world, pos).getComparatorOutput();
    }

    @Override
    public void renderHUD(MatrixStack ms, Minecraft mc, World world, BlockPos pos) {
        this.getTile(world, pos).renderHUD(ms, mc);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getOpacity(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return (!this.fullCube) ? 0 : super.getOpacity(state, world, pos);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return !this.fullCube;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTransparent(@Nonnull BlockState state) {
        return !this.fullCube;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return (!this.fullCube) ? FRAME_SHAPE : super.getShape(state, world, pos, context);
    }

    @Override
    protected boolean shouldDropInventory(World world, BlockPos pos, BlockState state) {
        return false;
    }
}
