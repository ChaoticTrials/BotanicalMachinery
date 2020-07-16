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

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends LootTableProvider {
    public LootTables(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(BlockTable::new, LootParameterSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((name, table) -> LootTableManager.func_227508_a_(validationtracker, name, table));
    }

    private class BlockTable extends BlockLootTables {
        @Override
        protected void addTables() {
            for (RegistryObject<Block> block : Registration.BLOCKS.getEntries()) {
                this.registerLootTable(block.get(), droppingWithMana(block.get()));
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Registration.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }

        protected LootTable.Builder droppingWithMana(Block block) {
            return LootTable.builder().addLootPool(withSurvivesExplosion(block, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block)
                    .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                            .replaceOperation("inv", "BlockEntityTag.inv")
                            .replaceOperation("mana", "BlockEntityTag.mana")))));
        }
    }
}
