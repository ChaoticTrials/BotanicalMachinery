package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.RegistryObject;
import vazkii.botania.common.lib.LibMisc;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, BotanicalMachinery.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (RegistryObject<Block> blockObject : Registration.BLOCKS.getEntries()) {
            Block block = blockObject.get();
            ModelFile model = models().orientable(block.getRegistryName().getPath(), new ResourceLocation(LibMisc.MOD_ID, "blocks/livingrock0"), modLoc("block/" + block.getRegistryName().getPath()), new ResourceLocation(LibMisc.MOD_ID, "blocks/livingrock0"));
            getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
        }
    }
}
