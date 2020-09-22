package de.melanx.botanicalmachinery.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static de.melanx.botanicalmachinery.core.TileTags.*;

public class LootTables extends LootTableProvider {

    public LootTables(DataGenerator gen) {
        super(gen);
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(BlockTable::new, LootParameterSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationTracker validationtracker) {
        map.forEach((name, table) -> LootTableManager.validateLootTable(validationtracker, name, table));
    }

    private static class BlockTable extends BlockLootTables {
        @Override
        protected void addTables() {
            for (RegistryObject<Block> block : Registration.BLOCKS.getEntries()) {
                if (block.get() == Registration.BLOCK_MANA_EMERALD.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get()));
                } else if (block.get() == Registration.BLOCK_ALFHEIM_MARKET.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS));
                } else if (block.get() == Registration.BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS));
                } else if (block.get() == Registration.BLOCK_MANA_BATTERY.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED));
                } else if (block.get() == Registration.BLOCK_MANA_BATTERY_CREATIVE.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS, SLOT_1_LOCKED, SLOT_2_LOCKED));
                } else if (block.get() == Registration.BLOCK_MECHANICAL_APOTHECARY.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS, FLUID, MAX_PROGRESS));
                } else if (block.get() == Registration.BLOCK_MECHANICAL_BREWERY.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS, MAX_PROGRESS));
                } else if (block.get() == Registration.BLOCK_MECHANICAL_DAISY.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, WORKING_TICKS));
                } else if (block.get() == Registration.BLOCK_MECHANICAL_MANA_POOL.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA));
                } else if (block.get() == Registration.BLOCK_MECHANICAL_RUNIC_ALTAR.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), INVENTORY, MANA, PROGRESS, MAX_PROGRESS));
                }
            }
        }

        @Nonnull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Registration.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }

        protected LootTable.Builder droppingWith(Block block, String... tags) {
            CopyNbt.Builder builder = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
            for (String tag : tags) {
                builder.replaceOperation(tag, "BlockEntityTag." + tag);
            }

            return LootTable.builder().addLootPool(withSurvivesExplosion(block, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block)
                    .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(builder))));
        }
    }
}
