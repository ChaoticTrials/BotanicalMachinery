package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.base.BaseBlock;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.registration.ModBlocks;
import de.melanx.botanicalmachinery.core.registration.Registration;
import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class BlockStates extends BlockStateProviderBase {
    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(BotanicalMachinery.getInstance(), gen, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        // todo
        Registration.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            if (block == ModBlocks.MECHANICAL_DAISY) {
                this.createStateForManualModel(builder, block);
            } else if (block == ModBlocks.ALFHEIM_MARKET
                    || block == ModBlocks.MECHANICAL_MANA_POOL
                    || block == ModBlocks.MECHANICAL_RUNIC_ALTAR
                    || block == ModBlocks.INDUSTRIAL_AGGLOMERATION_FACTORY
                    || block == ModBlocks.MECHANICAL_BREWERY
                    || block == ModBlocks.MECHANICAL_APOTHECARY) {
                this.createStateForManualModelRotatable(builder, block);
            } else if (block == ModBlocks.MANA_BATTERY) {
                this.createModels(builder, block, this.modLoc("block/" + LibNames.MANA_BATTERY + "_top"));
            } else if (block == ModBlocks.CREATIVE_MANA_BATTERY) {
                this.createModels(builder, block, this.modLoc("block/" + LibNames.MANA_BATTERY_CREATIVE + "_top"));
            } else if (block instanceof BaseBlock) {
                this.createModels(builder, block);
            } else {
                this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(this.modelDefault(block)).build());
            }
        });
    }

    private ModelFile modelDefault(Block block) {
        @SuppressWarnings("ConstantConditions")
        String name = block.getRegistryName().getPath();
        return this.models().cubeAll(name, this.modLoc("block/" + name));
    }

    private void createModels(VariantBlockStateBuilder builder, Block block, ResourceLocation top) {
        @SuppressWarnings("ConstantConditions")
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
        //noinspection ConstantConditions
        builder.partialState().addModels(new ConfiguredModel(this.models().getExistingFile(new ResourceLocation(BotanicalMachinery.getInstance().modid, "block/" + block.getRegistryName().getPath()))));
    }

    private void createStateForManualModelRotatable(VariantBlockStateBuilder builder, Block block) {
        @SuppressWarnings("ConstantConditions")
        ModelFile model = this.models().getExistingFile(new ResourceLocation(BotanicalMachinery.getInstance().modid, "block/" + block.getRegistryName().getPath()));
        for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
            builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, direction)
                    .addModels(new ConfiguredModel(model, direction.getHorizontalIndex() == -1 ? direction.getOpposite().getAxisDirection().getOffset() * 90 : 0, (int) direction.getOpposite().getHorizontalAngle(), false));
        }
    }
}
