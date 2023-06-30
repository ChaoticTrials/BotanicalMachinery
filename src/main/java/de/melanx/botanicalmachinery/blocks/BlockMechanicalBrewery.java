package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalBreweryRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalBrewery;
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

public class BlockMechanicalBrewery extends BotanicalBlock<BlockEntityMechanicalBrewery, ContainerMenuMechanicalBrewery> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(5, 1, 5, 6, 2, 6),
            box(5, 1, 10, 6, 2, 11),
            box(10, 1, 5, 11, 2, 6),
            box(10, 1, 10, 11, 2, 11),
            box(3, 2, 3, 13, 3, 13),
            box(3, 3, 12, 13, 8, 13),
            box(3, 3, 3, 13, 8, 4),
            box(12, 3, 4, 13, 8, 12),
            box(3, 3, 4, 4, 8, 12)
    ));

    public BlockMechanicalBrewery(ModX mod, Class<BlockEntityMechanicalBrewery> teClass, MenuType<ContainerMenuMechanicalBrewery> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.mechanicalBrewery.menu, ScreenMechanicalBrewery::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalBreweryRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
