package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class Languages extends LanguageProvider {
    public Languages(DataGenerator gen) {
        super(gen, BotanicalMachinery.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup." + BotanicalMachinery.MODID, "Botanical Machinery");
        this.addTileWithScreen(Registration.BLOCK_ALFHEIM_MARKET.get(), "Alfheim Market");
        this.addTileWithScreen(Registration.BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), "Industrial Agglomeration Factory");
        this.addTileWithScreen(Registration.BLOCK_MANA_BATTERY.get(), "Mana Battery");
        this.addTileWithScreen(Registration.BLOCK_MECHANICAL_BREWERY.get(), "Mechanical Brewery");
        this.addTileWithScreen(Registration.BLOCK_MECHANICAL_MANA_POOL.get(), "Mechanical Mana Pool");
        this.addTileWithScreen(Registration.BLOCK_MECHANICAL_RUNIC_ALTAR.get(), "Mechanical Runic Altar");
        this.addTileWithScreen(Registration.BLOCK_MECHANICAL_DAISY.get(), "Mechanical Daisy");
    }

    private void addTileWithScreen(Block key, String name) {
        this.add(key, name);
        //noinspection ConstantConditions
        this.add("screen." + BotanicalMachinery.MODID + "." + key.getRegistryName().getPath(), name);
    }
}
