package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalApothecaryRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalApothecary;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.MenuBlockBE;
import org.moddingx.libx.block.RotationShape;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;
import org.moddingx.libx.render.ItemStackRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockMechanicalApothecary extends MenuBlockBE<BlockEntityMechanicalApothecary, ContainerMenuMechanicalApothecary> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(0, 0, 0, 16, 1, 16),
            box(0, 1, 0, 1, 15, 1),
            box(15, 1, 0, 16, 15, 1),
            box(0, 1, 15, 1, 15, 16),
            box(15, 1, 15, 16, 15, 16),
            box(0, 15, 1, 1, 16, 15),
            box(15, 15, 1, 16, 16, 15),
            box(0, 15, 0, 16, 16, 1),
            box(0, 15, 15, 16, 16, 16),
            box(12, 10, 4, 13, 13, 12),
            box(3, 1, 3, 13, 2, 13),
            box(5, 2, 5, 11, 9, 11),
            box(3, 9, 3, 13, 10, 13),
            box(3, 10, 12, 13, 13, 13),
            box(3, 10, 3, 13, 13, 4),
            box(3, 10, 4, 4, 13, 12)
    ));

    public BlockMechanicalApothecary(ModX mod, Class<BlockEntityMechanicalApothecary> teClass, MenuType<ContainerMenuMechanicalApothecary> menu) {
        super(mod, teClass, menu, Properties.copy(Blocks.STONE).strength(2, 10).dynamicShape().forceSolidOn(), new Item.Properties());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        ItemStackRenderer.addRenderBlock(this.getBlockEntityType(), true);
        MenuScreens.register(ModBlocks.mechanicalApothecary.menu, ScreenMechanicalApothecary::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalApothecaryRenderer());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeItemClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ItemStackRenderer.createProperties());
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity tile = level.getBlockEntity(pos);

            ItemStack held = player.getMainHandItem();
            @SuppressWarnings("ConstantConditions")
            FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainer(held, tile.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null), 1000, player, true);
            if (fluidActionResult.isSuccess()) {
                if (tile instanceof BlockEntityBase) {
                    ((BlockEntityBase) tile).setDispatchable();
                }

                if (!player.isCreative()) {
                    player.setItemInHand(hand, fluidActionResult.getResult());
                }

                return InteractionResult.SUCCESS;
            }

            super.use(state, level, pos, player, hand, hit);
        }

        return InteractionResult.SUCCESS;
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

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    protected boolean shouldDropInventory(Level level, BlockPos pos, BlockState state) {
        return false;
    }
}
