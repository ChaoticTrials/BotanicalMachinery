package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.recipe.RecipeProviderBase;
import org.moddingx.libx.datagen.provider.recipe.crafting.CompressionExtension;
import org.moddingx.libx.datagen.provider.recipe.crafting.CraftingExtension;
import org.moddingx.libx.mod.ModX;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;

@Datagen
public class Recipes extends RecipeProviderBase implements CraftingExtension, CompressionExtension {

    public Recipes(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    protected void setup() {
        this.shaped(BotaniaItems.manaTablet, "SSS", "SPS", "SSS", 'S', BotaniaBlocks.livingrock, 'P', ModItems.manaEmerald);
        this.shaped(BotaniaBlocks.runeAltar, "SSS", "SPS", 'S', BotaniaBlocks.livingrock, 'P', ModItems.manaEmerald);
        this.compress(ModItems.manaEmerald, ModBlocks.manaEmeraldBlock);
        this.shaped(ModBlocks.manaBattery, "dgd", "grg", "dbd", 'd', BotaniaTags.Items.GEMS_DRAGONSTONE, 'g', BotaniaItems.gaiaIngot, 'r', BotaniaItems.manaRingGreater, 'b', ModBlocks.manaEmeraldBlock);
        this.shaped(ModBlocks.mechanicalDaisy, " d ", "eae", 'e', BotaniaTags.Items.BLOCKS_ELEMENTIUM, 'a', BotaniaItems.auraRingGreater, 'd', BotaniaFlowerBlocks.pureDaisyFloating);
        this.defaultMachine(ModBlocks.alfheimMarket, BotaniaBlocks.alfPortal, BotaniaBlocks.livingwoodGlimmering, BotaniaBlocks.dreamwood, BotaniaBlocks.livingwoodGlimmering);
        this.defaultMachine(ModBlocks.industrialAgglomerationFactory, BotaniaBlocks.terraPlate, BotaniaTags.Items.GEMS_MANA_DIAMOND, BotaniaTags.Items.INGOTS_MANASTEEL, BotaniaItems.manaPearl);
        this.defaultMachine(ModBlocks.mechanicalManaPool, BotaniaBlocks.fabulousPool, BotaniaBlocks.alchemyCatalyst, BotaniaBlocks.dilutedPool, BotaniaBlocks.conjurationCatalyst);
        this.defaultMachine(ModBlocks.mechanicalRunicAltar, BotaniaBlocks.runeAltar, Ingredient.of(
                BotaniaItems.runeLust,
                BotaniaItems.runeGluttony,
                BotaniaItems.runeGreed,
                BotaniaItems.runeSloth,
                BotaniaItems.runeWrath,
                BotaniaItems.runeEnvy,
                BotaniaItems.runePride)
        );
        this.defaultMachine(ModBlocks.mechanicalBrewery, BotaniaBlocks.brewery, BotaniaItems.flask, Items.BLAZE_ROD, BotaniaItems.flask);
        this.defaultMachine(ModBlocks.mechanicalApothecary, BotaniaBlocks.defaultAltar, BotaniaTags.Items.PETALS);
    }

    private void defaultMachine(Object output, Object special1, Object special2, Object special3, Object special4) {
        this.shaped(output, "eye", "xaz", "ese", 'e', BotaniaTags.Items.INGOTS_ELEMENTIUM, 'a', BotaniaItems.auraRingGreater, 's', special1, 'x', special2, 'y', special3, 'z', special4);
    }

    private void defaultMachine(Object output, Object special1, Object special2) {
        this.shaped(output, "exe", "xax", "ese", 'e', BotaniaTags.Items.INGOTS_ELEMENTIUM, 'a', BotaniaItems.auraRingGreater, 's', special1, 'x', special2);
    }
}
