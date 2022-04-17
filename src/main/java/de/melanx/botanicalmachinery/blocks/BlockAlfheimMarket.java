package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.screens.ScreenAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tesr.AlfheimMarketRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityAlfheimMarket;
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

public class BlockAlfheimMarket extends BotanicalBlock<BlockEntityAlfheimMarket, ContainerMenuAlfheimMarket> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(4.4, 1, 8.8, 11.6, 13, 11.2),
            box(0, 0, 8.8, 0, 0, 11.2),
            box(3.2, 0, 3.6, 6.8, 7.4, 7.2),
            box(8.8, 0, 3.6, 12.4, 7.4, 7.2)
    ));

    public BlockAlfheimMarket(ModX mod, Class<BlockEntityAlfheimMarket> teClass, MenuType<ContainerMenuAlfheimMarket> menu) {
        super(mod, teClass, menu, false, true);
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        super.registerClient(id, defer);
        MenuScreens.register(ModBlocks.alfheimMarket.menu, ScreenAlfheimMarket::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new AlfheimMarketRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
