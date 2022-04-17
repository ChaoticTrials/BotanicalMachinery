package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalDaisy;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalDaisy;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalDaisyRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalDaisy;
import io.github.noeppi_noeppi.libx.base.tile.MenuBlockBE;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

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
        super(mod, teClass, menu, Properties.of(Material.STONE).strength(2, 10).dynamicShape(),
                new Item.Properties());
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutout());
        ItemStackRenderer.addRenderBlock(this.getBlockEntityType(), true);
        MenuScreens.register(ModBlocks.mechanicalDaisy.menu, ScreenMechanicalDaisy::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalDaisyRenderer());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeItemClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
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
