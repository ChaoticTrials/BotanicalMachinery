package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.BlockManaBattery;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, BotanicalMachinery.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        Registration.BLOCKS.getEntries().stream().map(block -> block.get()).forEach(block -> {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            if (block == Registration.BLOCK_MECHANICAL_DAISY.get()) {
                this.createStateForManualModel(builder, block);
            } else if (block instanceof BlockManaBattery) {
                this.createModels(builder, block, this.modLoc("block/" + LibNames.MANA_BATTERY + "_top"));
            } else {
                this.createModels(builder, block);
            }
        });
    }

    private void createModels(VariantBlockStateBuilder builder, Block block, ResourceLocation top) {
        String name = block.getRegistryName().getPath();
        ModelFile model = this.models().orientable(block.getRegistryName().getPath(), this.modLoc("block/" + name + "_side"), this.modLoc("block/" + name + "_front"), top);
        for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
            builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, direction)
                    .addModels(new ConfiguredModel(model, direction.getHorizontalIndex() == -1 ? direction.getOpposite().getAxisDirection().getOffset() * 90 : 0, (int) direction.getOpposite().getHorizontalAngle(), false));
        }
    }

    private void createModels(VariantBlockStateBuilder builder, Block block) {
        this.createModels(builder, block, this.modLoc("block/machine_top"));
    }

    private void createStateForManualModel(VariantBlockStateBuilder builder, Block block) {
        builder.partialState().addModels(new ConfiguredModel(this.models().getExistingFile(new ResourceLocation(BotanicalMachinery.MODID, "block/" + block.getRegistryName().getPath()))));
    }
}
