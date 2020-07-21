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
        add("itemGroup." + BotanicalMachinery.MODID, "Botanical Machinery");
        addTileWithScreen(Registration.BLOCK_MECHANICAL_MANA_POOL.get(), "Machanical Mana Pool");
        addTileWithScreen(Registration.BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), "Industrial Agglomeration Factory");
    }

    private void addTileWithScreen(Block key, String name) {
        add(key, name);
        add("screen." + BotanicalMachinery.MODID + "." + key.getRegistryName().getPath(), name);
    }
}
