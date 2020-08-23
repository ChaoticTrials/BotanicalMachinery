package de.melanx.botanicalmachinery.blocks.base;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.botania.api.wand.IWandHUD;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public abstract class BlockBase extends Block implements ITileEntityProvider, IWandHUD {

    private static final VoxelShape RENDER_SHAPE_NO_CULLFACE = makeCuboidShape(0.001, 0.001, 0.001, 15.999, 15.999, 15.999);

    private final boolean fullCube;

    public BlockBase(boolean fullCube) {
        super(fullCube ? Properties.create(Material.ROCK).hardnessAndResistance(2, 10) : Properties.create(Material.ROCK).hardnessAndResistance(2, 10).variableOpacity());
        this.fullCube = fullCube;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof INamedContainerProvider ? (INamedContainerProvider) tile : null;
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
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        //noinspection ConstantConditions
        ((TileBase) world.getTileEntity(pos)).renderHUD(mc);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        ContainerType<?> containerType = this.getContainerType();
        if (containerType != null) {
            if (!world.isRemote) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        //noinspection ConstantConditions
                        return new TranslationTextComponent("screen." + BotanicalMachinery.MODID + "." + BlockBase.this.getRegistryName().getPath());
                    }

                    @Override
                    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
                        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                        buffer.writeBlockPos(pos);
                        return containerType.create(windowId, playerInventory, buffer);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
            }
            return ActionResultType.SUCCESS;
        } else {
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }
    }

    @Nullable
    protected ContainerType<?> getContainerType() {
        return null;
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
    public boolean isTransparent(@Nonnull BlockState state) {
        return !this.fullCube;
    }

    @Nonnull
    @Override
    public VoxelShape getRenderShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return (!this.fullCube) ? RENDER_SHAPE_NO_CULLFACE : super.getRenderShape(state, world, pos);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return (!this.fullCube) ? RENDER_SHAPE_NO_CULLFACE : super.getShape(state, world, pos, context);
    }
}
