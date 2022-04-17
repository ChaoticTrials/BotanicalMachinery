package de.melanx.botanicalmachinery.blocks.base;

import io.github.noeppi_noeppi.libx.base.tile.MenuBlockBE;
import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class BotanicalBlock<T extends BotanicalTile, C extends BlockEntityMenu<T>> extends MenuBlockBE<T, C> {

    public static final VoxelShape FRAME_SHAPE = Shapes.or(
            box(0, 0, 0, 16, 1, 16),
            box(0, 0, 0, 1, 16, 1),
            box(15, 0, 0, 16, 16, 1),
            box(0, 0, 15, 1, 16, 16),
            box(15, 0, 15, 16, 16, 16),
            box(0, 15, 0, 1, 16, 16),
            box(0, 15, 0, 16, 16, 1),
            box(15, 15, 0, 16, 16, 16),
            box(0, 15, 15, 16, 16, 16)
    );

    public final boolean fullCube;
    public final boolean specialRender;

    public BotanicalBlock(ModX mod, Class<T> teClass, MenuType<C> menu, boolean fullCube, boolean specialRender) {
        super(mod, teClass, menu, fullCube ?
                        Properties.of(Material.STONE).strength(2, 10)
                        : Properties.of(Material.STONE).strength(2, 10).dynamicShape().noOcclusion(),
                new Item.Properties());
        this.fullCube = fullCube;
        this.specialRender = specialRender;
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        if (this.specialRender) {
            ItemStackRenderer.addRenderBlock(this.getBlockEntityType(), true);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeItemClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
        consumer.accept(ItemStackRenderer.createProperties());
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BotanicalTile blockEntity = (BotanicalTile) level.getBlockEntity(pos);
        if (blockEntity == null) return null;

        CompoundTag tag = blockEntity.serializeNBT();
        ItemStack stack = new ItemStack(this);
        stack.setTag(tag);
        return stack;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
        return this.getBlockEntity(level, pos).getComparatorOutput();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return (!this.fullCube) ? 0 : super.getLightBlock(state, level, pos);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
        return !this.fullCube;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return !this.fullCube;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return (!this.fullCube) ? FRAME_SHAPE : super.getShape(state, level, pos, context);
    }

    @Override
    protected boolean shouldDropInventory(Level level, BlockPos pos, BlockState state) {
        return false;
    }
}
