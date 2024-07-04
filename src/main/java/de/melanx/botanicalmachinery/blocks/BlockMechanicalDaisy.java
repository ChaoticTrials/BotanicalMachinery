package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalDaisy;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalDaisy;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalDaisyRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalDaisy;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.moddingx.libx.base.tile.MenuBlockBE;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;
import org.moddingx.libx.render.ItemStackRenderer;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockMechanicalDaisy extends MenuBlockBE<BlockEntityMechanicalDaisy, ContainerMenuMechanicalDaisy> {

    private static final VoxelShape COLLISION_SHAPE = Shapes.joinUnoptimized(
            box(0, 0, 0, 16, 2, 16),
            box(5, 2, 5, 11, 3, 11),
            BooleanOp.OR
    );

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 11.4, 16);

    public BlockMechanicalDaisy(ModX mod, Class<BlockEntityMechanicalDaisy> teClass, MenuType<ContainerMenuMechanicalDaisy> menu) {
        super(mod, teClass, menu, Properties.copy(Blocks.STONE).strength(2, 10).dynamicShape().forceSolidOn(),
                new Item.Properties());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        ItemStackRenderer.addRenderBlock(this.getBlockEntityType(), true);
        MenuScreens.register(ModBlocks.mechanicalDaisy.menu, ScreenMechanicalDaisy::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalDaisyRenderer());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeItemClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ItemStackRenderer.createProperties());
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
    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getOcclusionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected boolean shouldDropInventory(Level level, BlockPos pos, BlockState state) {
        return false;
    }
}
