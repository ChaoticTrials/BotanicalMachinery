package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.ModItems;
import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import io.github.noeppi_noeppi.libx.data.provider.recipe.crafting.CompressionExtension;
import io.github.noeppi_noeppi.libx.data.provider.recipe.crafting.CraftingExtension;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.botania.common.block.ModSubtiles;
import vazkii.botania.common.lib.ModTags;

@Datagen
public class Recipes extends RecipeProviderBase implements CraftingExtension, CompressionExtension {

    public Recipes(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    protected void setup() {
        this.shaped(vazkii.botania.common.item.ModItems.manaTablet, "SSS", "SPS", "SSS", 'S', vazkii.botania.common.block.ModBlocks.livingrock, 'P', ModItems.manaEmerald);
        this.shaped(vazkii.botania.common.block.ModBlocks.runeAltar, "SSS", "SPS", 'S', vazkii.botania.common.block.ModBlocks.livingrock, 'P', ModItems.manaEmerald);
        this.compress(ModItems.manaEmerald, ModBlocks.manaEmeraldBlock);
        this.shaped(ModBlocks.manaBattery, "dgd", "grg", "dbd", 'd', ModTags.Items.GEMS_DRAGONSTONE, 'g', vazkii.botania.common.item.ModItems.gaiaIngot, 'r', vazkii.botania.common.item.ModItems.manaRingGreater, 'b', ModBlocks.manaEmeraldBlock);
        this.shaped(ModBlocks.mechanicalDaisy, " d ", "eae", 'e', ModTags.Items.BLOCKS_ELEMENTIUM, 'a', vazkii.botania.common.item.ModItems.auraRingGreater, 'd', ModSubtiles.pureDaisyFloating);
        this.defaultMachine(ModBlocks.alfheimMarket, vazkii.botania.common.block.ModBlocks.alfPortal, vazkii.botania.common.block.ModBlocks.livingwoodGlimmering, vazkii.botania.common.block.ModBlocks.dreamwood, vazkii.botania.common.block.ModBlocks.livingwoodGlimmering);
        this.defaultMachine(ModBlocks.industrialAgglomerationFactory, vazkii.botania.common.block.ModBlocks.terraPlate, ModTags.Items.GEMS_MANA_DIAMOND, ModTags.Items.INGOTS_MANASTEEL, vazkii.botania.common.item.ModItems.manaPearl);
        this.defaultMachine(ModBlocks.mechanicalManaPool, vazkii.botania.common.block.ModBlocks.fabulousPool, vazkii.botania.common.block.ModBlocks.alchemyCatalyst, vazkii.botania.common.block.ModBlocks.dilutedPool, vazkii.botania.common.block.ModBlocks.conjurationCatalyst);
        this.defaultMachine(ModBlocks.mechanicalRunicAltar, vazkii.botania.common.block.ModBlocks.runeAltar, Ingredient.of(
                vazkii.botania.common.item.ModItems.runeLust,
                vazkii.botania.common.item.ModItems.runeGluttony,
                vazkii.botania.common.item.ModItems.runeGreed,
                vazkii.botania.common.item.ModItems.runeSloth,
                vazkii.botania.common.item.ModItems.runeWrath,
                vazkii.botania.common.item.ModItems.runeEnvy,
                vazkii.botania.common.item.ModItems.runePride)
        );
        this.defaultMachine(ModBlocks.mechanicalBrewery, vazkii.botania.common.block.ModBlocks.brewery, vazkii.botania.common.item.ModItems.flask, Items.BLAZE_ROD, vazkii.botania.common.item.ModItems.flask);
        this.defaultMachine(ModBlocks.mechanicalApothecary, vazkii.botania.common.block.ModBlocks.defaultAltar, ModTags.Items.PETALS);
    }

    private void defaultMachine(Object output, Object special1, Object special2, Object special3, Object special4) {
        shaped(output, "eye", "xaz", "ese", 'e', ModTags.Items.INGOTS_ELEMENTIUM, 'a', vazkii.botania.common.item.ModItems.auraRingGreater, 's', special1, 'x', special2, 'y', special3, 'z', special4);
    }
    
    private void defaultMachine(Object output, Object special1, Object special2) {
        shaped(output, "exe", "xax", "ese", 'e', ModTags.Items.INGOTS_ELEMENTIUM, 'a', vazkii.botania.common.item.ModItems.auraRingGreater, 's', special1, 'x', special2);
    }
}
