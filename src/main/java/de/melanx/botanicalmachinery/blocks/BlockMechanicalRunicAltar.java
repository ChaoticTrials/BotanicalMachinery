package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalRunicAltarRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalRunicAltar;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockMechanicalRunicAltar extends BotanicalBlock<BlockEntityMechanicalRunicAltar, ContainerMenuMechanicalRunicAltar> {

    public static final RotationShape SHAPE = new RotationShape(Shapes.or(
            BotanicalBlock.FRAME_SHAPE,
            box(2, 5, 2, 14, 9, 14),
            box(6, 3, 6, 10, 5, 10),
            box(4, 1, 4, 12, 3, 12)
    ));

    public BlockMechanicalRunicAltar(ModX mod, Class<BlockEntityMechanicalRunicAltar> teClass, MenuType<ContainerMenuMechanicalRunicAltar> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        super.registerClient(id, defer);
        MenuScreens.register(ModBlocks.mechanicalRunicAltar.menu, ScreenMechanicalRunicAltar::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalRunicAltarRenderer());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
