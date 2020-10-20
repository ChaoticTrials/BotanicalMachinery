package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import io.github.noeppi_noeppi.libx.data.provider.BlockLootProviderBase;
import net.minecraft.data.DataGenerator;

import static de.melanx.botanicalmachinery.core.TileTags.*;

public class LootTables extends BlockLootProviderBase {

    public LootTables(DataGenerator gen) {
        super(BotanicalMachinery.getInstance(), gen);
    }

    @Override
    protected void setup() {
        this.copyNBT(ModBlocks.alfheimMarket, INVENTORY, MANA, PROGRESS);
        this.copyNBT(ModBlocks.industrialAgglommerationFactory, INVENTORY, MANA, PROGRESS);
        this.copyNBT(ModBlocks.manaBattery, INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED);
        this.copyNBT(ModBlocks.manaBatteryCreative, INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED);
        this.copyNBT(ModBlocks.mechanicalApothecary, INVENTORY, MANA, PROGRESS, FLUID, MAX_PROGRESS);
        this.copyNBT(ModBlocks.mechanicalBrewery, INVENTORY, MANA, PROGRESS, MAX_PROGRESS);
        this.copyNBT(ModBlocks.mechanicalDaisy, INVENTORY, WORKING_TICKS);
        this.copyNBT(ModBlocks.mechanicalManaPool, INVENTORY, MANA);
        this.copyNBT(ModBlocks.mechanicalRunicAltar, INVENTORY, MANA, PROGRESS, MAX_PROGRESS);
    }
}
