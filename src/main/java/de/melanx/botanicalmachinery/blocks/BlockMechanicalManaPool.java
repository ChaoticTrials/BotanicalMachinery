package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalManaPoolRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import io.github.noeppi_noeppi.libx.block.RotationShape;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockMechanicalManaPool extends BotanicalBlock<BlockEntityMechanicalManaPool, ContainerMenuMechanicalManaPool> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(2, 1, 2, 14, 1.1, 14),
            box(2, 1, 13, 14, 6, 14),
            box(2, 1, 2, 14, 6, 3),
            box(13, 1, 3, 14, 6, 13),
            box(2, 1, 3, 3, 6, 13)
    ));

    public BlockMechanicalManaPool(ModX mod, Class<BlockEntityMechanicalManaPool> teClass, MenuType<ContainerMenuMechanicalManaPool> menu) {
        super(mod, teClass, menu, false, true);
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        super.registerClient(id, defer);
        MenuScreens.register(ModBlocks.mechanicalManaPool.menu, ScreenMechanicalManaPool::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalManaPoolRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
