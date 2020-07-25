package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, BotanicalMachinery.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        Registration.BLOCKS.getEntries().stream().map(block -> block.get()).forEach(block -> {
            VariantBlockStateBuilder builder = getVariantBuilder(block);
            String name = block.getRegistryName().getPath();
            ModelFile model = models().orientable(block.getRegistryName().getPath(), modLoc("block/" + name + "_side"), modLoc("block/" + name + "_front"), modLoc("block/machine_top"));
            for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
                builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, direction)
                        .addModels(new ConfiguredModel(model, direction.getHorizontalIndex() == -1 ? direction.getOpposite().getAxisDirection().getOffset() * 90 : 0, (int) direction.getOpposite().getHorizontalAngle(), false));
            }
        });
    }
}
