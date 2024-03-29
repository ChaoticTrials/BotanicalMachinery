package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.loot.BlockLootProviderBase;

import static de.melanx.botanicalmachinery.core.TileTags.*;

public class LootTables extends BlockLootProviderBase {

    public LootTables(DatagenContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        this.drops(ModBlocks.alfheimMarket, this.copyNBT(INVENTORY, MANA, PROGRESS));
        this.drops(ModBlocks.industrialAgglomerationFactory, this.copyNBT(INVENTORY, MANA, PROGRESS));
        this.drops(ModBlocks.manaBattery, this.copyNBT(INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED));
        this.drops(ModBlocks.manaBatteryCreative, this.copyNBT(INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED));
        this.drops(ModBlocks.mechanicalApothecary, this.copyNBT(INVENTORY, MANA, PROGRESS, FLUID, MAX_PROGRESS));
        this.drops(ModBlocks.mechanicalBrewery, this.copyNBT(INVENTORY, MANA, PROGRESS, MAX_PROGRESS));
        this.drops(ModBlocks.mechanicalDaisy, this.copyNBT(INVENTORY, WORKING_TICKS));
        this.drops(ModBlocks.mechanicalManaPool, this.copyNBT(INVENTORY, MANA));
        this.drops(ModBlocks.mechanicalRunicAltar, this.copyNBT(INVENTORY, MANA, PROGRESS, MAX_PROGRESS));
    }
}
