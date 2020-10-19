package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModSubtiles;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Recipes extends RecipeProviderBase {

    public Recipes(DataGenerator generator) {
        super(BotanicalMachinery.getInstance(), generator);
        generator.addProvider(new ManaInfusionProvider(generator));
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        this.registerExtraRecipes(consumer);

        this.makeBlockItem(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.MANA_EMERALD, de.melanx.botanicalmachinery.core.registration.ModItems.MANA_EMERALD);

        this.shaped(de.melanx.botanicalmachinery.core.registration.ModBlocks.MANA_BATTERY)
                .key('d', ModTags.Items.GEMS_DRAGONSTONE)
                .key('g', ModItems.gaiaIngot)
                .key('r', ModItems.manaRingGreater)
                .key('b', de.melanx.botanicalmachinery.core.registration.ModBlocks.MANA_EMERALD.asItem())
                .patternLine("dgd")
                .patternLine("grg")
                .patternLine("dbd")
                .addCriterion("has_item", this.hasItem(ModItems.manaRingGreater))
                .build(consumer);

        this.shaped(de.melanx.botanicalmachinery.core.registration.ModBlocks.MECHANICAL_DAISY)
                .key('e', ModTags.Items.BLOCKS_ELEMENTIUM)
                .key('a', ModItems.auraRingGreater)
                .key('d', ModSubtiles.pureDaisyFloating)
                .patternLine(" d ")
                .patternLine("eae")
                .addCriterion("has_item", this.hasItem(ModSubtiles.pureDaisyFloating))
                .build(consumer);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.ALFHEIM_MARKET, ModBlocks.alfPortal,
                ModBlocks.livingwoodGlimmering, ModBlocks.dreamwood, ModBlocks.livingwoodGlimmering);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.INDUSTRIAL_AGGLOMERATION_FACTORY, ModBlocks.terraPlate,
                Ingredient.fromTag(ModTags.Items.GEMS_MANA_DIAMOND),
                Ingredient.fromTag(ModTags.Items.INGOTS_MANASTEEL),
                Ingredient.fromItems(ModItems.manaPearl));

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.MECHANICAL_MANA_POOL, ModBlocks.fabulousPool,
                ModBlocks.alchemyCatalyst, ModBlocks.dilutedPool, ModBlocks.conjurationCatalyst);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.MECHANICAL_RUNIC_ALTAR, ModBlocks.runeAltar,
                Ingredient.fromItems(ModItems.runeLust, ModItems.runeGluttony, ModItems.runeGreed,
                        ModItems.runeSloth, ModItems.runeWrath, ModItems.runeEnvy, ModItems.runePride));

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.MECHANICAL_BREWERY, ModBlocks.brewery,
                ModItems.flask, Items.BLAZE_ROD, ModItems.flask);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.MECHANICAL_APOTHECARY, ModBlocks.defaultAltar,
                Ingredient.fromTag(ModTags.Items.PETALS));
    }

    private ShapedRecipeBuilder shaped(IItemProvider result) {
        //noinspection ConstantConditions
        return ShapedRecipeBuilder.shapedRecipe(result).setGroup(BotanicalMachinery.getInstance().modid + ":" + result.asItem().getRegistryName().getPath());
    }

    private void compress(Consumer<IFinishedRecipe> consumer, IItemProvider output, Item input) {
        //noinspection ConstantConditions
        this.shaped(output)
                .key('X', input)
                .patternLine("XXX")
                .patternLine("XXX")
                .patternLine("XXX")
                .addCriterion("has_item", this.hasItem(input))
                .build(consumer, new ResourceLocation(BotanicalMachinery.getInstance().modid, "compress/" + output.asItem().getRegistryName().getPath()));
    }

    private void decompress(Consumer<IFinishedRecipe> consumer, IItemProvider output, Item input) {
        //noinspection ConstantConditions
        ShapelessRecipeBuilder.shapelessRecipe(output, 9)
                .addIngredient(input)
                .addCriterion("has_item", this.hasItem(input))
                .build(consumer, new ResourceLocation(BotanicalMachinery.getInstance().modid, "decompress/" + output.asItem().getRegistryName().getPath()));
    }

    private void defaultMachine(Consumer<IFinishedRecipe> consumer, IItemProvider output, IItemProvider special1, IItemProvider special2) {
        this.defaultMachine(consumer, output, special1, Ingredient.fromItems(special2.asItem()));
    }

    private void defaultMachine(Consumer<IFinishedRecipe> consumer, IItemProvider output, IItemProvider special1, Ingredient special2) {
        this.defaultMachine(consumer, output, special1, special2, special2, special2);
    }

    private void defaultMachine(Consumer<IFinishedRecipe> consumer, IItemProvider output, IItemProvider special1, IItemProvider special2, IItemProvider special3, IItemProvider special4) {
        this.defaultMachine(consumer, output, special1, Ingredient.fromItems(special2.asItem()), Ingredient.fromItems(special3.asItem()), Ingredient.fromItems(special4.asItem()));
    }

    private void defaultMachine(Consumer<IFinishedRecipe> consumer, IItemProvider output, IItemProvider special1, Ingredient special2, Ingredient special3, Ingredient special4) {
        this.shaped(output)
                .key('e', ModTags.Items.INGOTS_ELEMENTIUM)
                .key('a', ModItems.auraRingGreater)
                .key('s', special1)
                .key('x', special2)
                .key('y', special3)
                .key('z', special4)
                .patternLine("eye")
                .patternLine("xaz")
                .patternLine("ese")
                .addCriterion("has_item", this.hasItem(special1))
                .build(consumer);
    }

    private void registerExtraRecipes(Consumer<IFinishedRecipe> consumer) {
        this.shaped(ModItems.manaTablet)
                .key('P', Ingredient.fromItems(de.melanx.botanicalmachinery.core.registration.ModItems.MANA_EMERALD))
                .key('S', ModTags.Items.LIVINGROCK)
                .patternLine("SSS")
                .patternLine("SPS")
                .patternLine("SSS")
                .addCriterion("has_item", this.hasItem(de.melanx.botanicalmachinery.core.registration.ModItems.MANA_EMERALD))
                .build(consumer, this.changedBotaniaLoc(ModItems.manaTablet));

        this.shaped(ModBlocks.runeAltar)
                .key('P', de.melanx.botanicalmachinery.core.registration.ModItems.MANA_EMERALD)
                .key('S', ModTags.Items.LIVINGROCK)
                .patternLine("SSS")
                .patternLine("SPS")
                .addCriterion("has_item", this.hasItem(de.melanx.botanicalmachinery.core.registration.ModItems.MANA_EMERALD))
                .build(consumer, this.changedBotaniaLoc(ModBlocks.runeAltar.asItem()));
    }

    private ResourceLocation changedBotaniaLoc(Item item) {
        @SuppressWarnings("ConstantConditions")
        String name = item.asItem().getRegistryName().getPath();
        return new ResourceLocation(BotanicalMachinery.getInstance().modid, "botania/" + name);
    }
}
