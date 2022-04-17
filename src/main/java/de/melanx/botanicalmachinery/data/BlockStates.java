package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

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
        this.manualModel(ModBlocks.manaBattery, this.models().orientable(ModBlocks.manaBattery.getRegistryName().getPath(),
                this.modLoc("block/" + ModBlocks.manaBattery.getRegistryName().getPath() + "_side"),
                this.modLoc("block/" + ModBlocks.manaBattery.getRegistryName().getPath() + "_front"),
                this.modLoc("block/" + ModBlocks.manaBattery.getRegistryName().getPath() + "_top")));

        //noinspection ConstantConditions
        this.manualModel(ModBlocks.manaBatteryCreative, this.models().orientable(ModBlocks.manaBatteryCreative.getRegistryName().getPath(),
                this.modLoc("block/" + ModBlocks.manaBatteryCreative.getRegistryName().getPath() + "_side"),
                this.modLoc("block/" + ModBlocks.manaBatteryCreative.getRegistryName().getPath() + "_front"),
                this.modLoc("block/" + ModBlocks.manaBatteryCreative.getRegistryName().getPath() + "_top")));
    }
}
