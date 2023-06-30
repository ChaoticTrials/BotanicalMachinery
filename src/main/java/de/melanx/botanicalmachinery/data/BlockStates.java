package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.BlockStateProviderBase;

@Datagen
public class BlockStates extends BlockStateProviderBase {

    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(BotanicalMachinery.getInstance(), gen, helper);
    }

    @Override
    protected void setup() {
        this.manualModel(ModBlocks.mechanicalDaisy);
        this.manualModel(ModBlocks.alfheimMarket);
        this.manualModel(ModBlocks.mechanicalManaPool);
        this.manualModel(ModBlocks.mechanicalRunicAltar);
        this.manualModel(ModBlocks.industrialAgglomerationFactory);
        this.manualModel(ModBlocks.mechanicalBrewery);
        this.manualModel(ModBlocks.mechanicalApothecary);

        //noinspection ConstantConditions
        this.manualModel(ModBlocks.manaBattery, this.models().orientable(ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBattery).getPath(),
                this.modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBattery).getPath() + "_side"),
                this.modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBattery).getPath() + "_front"),
                this.modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBattery).getPath() + "_top")));

        //noinspection ConstantConditions
        this.manualModel(ModBlocks.manaBatteryCreative, this.models().orientable(ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBatteryCreative).getPath(),
                this.modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBatteryCreative).getPath() + "_side"),
                this.modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBatteryCreative).getPath() + "_front"),
                this.modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModBlocks.manaBatteryCreative).getPath() + "_top")));
    }
}
