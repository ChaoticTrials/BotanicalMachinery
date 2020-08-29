package de.melanx.botanicalmachinery.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.CopyName;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends LootTableProvider {

    public static final String TAG_INVENTORY = "inv";
    public static final String TAG_MANA = "mana";
    public static final String TAG_WORKING_TICKS = "workingTicks";
    public static final String TAG_SLOT1_LOCKED = "slot1Locked";
    public static final String TAG_SLOT2_LOCKED = "slot2Locked";

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
        map.forEach((name, table) -> LootTableManager.func_227508_a_(validationtracker, name, table));
    }

    private static class BlockTable extends BlockLootTables {
        @Override
        protected void addTables() {
            for (RegistryObject<Block> block : Registration.BLOCKS.getEntries()) {
                if (block.get() == Registration.BLOCK_MECHANICAL_DAISY.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), TAG_INVENTORY, TAG_WORKING_TICKS));
                } else if (block.get() == Registration.BLOCK_MANA_BATTERY.get()
                        || block.get() == Registration.BLOCK_MANA_BATTERY_CREATIVE.get()) {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), TAG_INVENTORY, TAG_MANA, TAG_SLOT1_LOCKED, TAG_SLOT2_LOCKED));
                } else {
                    this.registerLootTable(block.get(), this.droppingWith(block.get(), TAG_INVENTORY, TAG_MANA));
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
