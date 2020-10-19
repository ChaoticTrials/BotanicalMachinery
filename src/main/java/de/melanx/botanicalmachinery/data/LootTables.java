package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.data.provider.BlockLootProviderBase;
import net.minecraft.data.DataGenerator;

import static de.melanx.botanicalmachinery.core.TileTags.*;

public class LootTables extends BlockLootProviderBase {

    public LootTables(DataGenerator gen) {
        super(BotanicalMachinery.getInstance(), gen);
        BlockLootProviderBase.copyNBT(ModBlocks.ALFHEIM_MARKET, INVENTORY, MANA, PROGRESS);
        BlockLootProviderBase.copyNBT(ModBlocks.INDUSTRIAL_AGGLOMERATION_FACTORY, INVENTORY, MANA, PROGRESS);
        BlockLootProviderBase.copyNBT(ModBlocks.MANA_BATTERY, INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED);
        BlockLootProviderBase.copyNBT(ModBlocks.CREATIVE_MANA_BATTERY, INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED);
        BlockLootProviderBase.copyNBT(ModBlocks.MECHANICAL_APOTHECARY, INVENTORY, MANA, PROGRESS, FLUID, MAX_PROGRESS);
        BlockLootProviderBase.copyNBT(ModBlocks.MECHANICAL_BREWERY, INVENTORY, MANA, PROGRESS, MAX_PROGRESS);
        BlockLootProviderBase.copyNBT(ModBlocks.MECHANICAL_DAISY, INVENTORY, WORKING_TICKS);
        BlockLootProviderBase.copyNBT(ModBlocks.MECHANICAL_MANA_POOL, INVENTORY, MANA);
        BlockLootProviderBase.copyNBT(ModBlocks.MECHANICAL_RUNIC_ALTAR, INVENTORY, MANA, PROGRESS, MAX_PROGRESS);
    }
}
