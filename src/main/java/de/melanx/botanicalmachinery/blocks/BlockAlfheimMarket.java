package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.screens.ScreenAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tesr.AlfheimMarketRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityAlfheimMarket;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.block.RotationShape;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.alfheimMarket.menu, ScreenAlfheimMarket::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new AlfheimMarketRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
