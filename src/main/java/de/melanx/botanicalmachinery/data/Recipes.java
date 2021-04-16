package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.ModItems;
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
import vazkii.botania.common.block.ModSubtiles;
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

        this.makeBlockItem(consumer, ModBlocks.manaEmeraldBlock, ModItems.manaEmerald);

        this.shaped(de.melanx.botanicalmachinery.ModBlocks.manaBattery)
                .key('d', ModTags.Items.GEMS_DRAGONSTONE)
                .key('g', vazkii.botania.common.item.ModItems.gaiaIngot)
                .key('r', vazkii.botania.common.item.ModItems.manaRingGreater)
                .key('b', de.melanx.botanicalmachinery.ModBlocks.manaEmeraldBlock.asItem())
                .patternLine("dgd")
                .patternLine("grg")
                .patternLine("dbd")
                .addCriterion("has_item", hasItem(vazkii.botania.common.item.ModItems.manaRingGreater))
                .build(consumer);

        this.shaped(de.melanx.botanicalmachinery.ModBlocks.mechanicalDaisy)
                .key('e', ModTags.Items.BLOCKS_ELEMENTIUM)
                .key('a', vazkii.botania.common.item.ModItems.auraRingGreater)
                .key('d', ModSubtiles.pureDaisyFloating)
                .patternLine(" d ")
                .patternLine("eae")
                .addCriterion("has_item", hasItem(ModSubtiles.pureDaisyFloating))
                .build(consumer);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.ModBlocks.alfheimMarket, vazkii.botania.common.block.ModBlocks.alfPortal,
                vazkii.botania.common.block.ModBlocks.livingwoodGlimmering,
                vazkii.botania.common.block.ModBlocks.dreamwood,
                vazkii.botania.common.block.ModBlocks.livingwoodGlimmering);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.ModBlocks.industrialAgglomerationFactory, vazkii.botania.common.block.ModBlocks.terraPlate,
                Ingredient.fromTag(ModTags.Items.GEMS_MANA_DIAMOND),
                Ingredient.fromTag(ModTags.Items.INGOTS_MANASTEEL),
                Ingredient.fromItems(vazkii.botania.common.item.ModItems.manaPearl));

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.ModBlocks.mechanicalManaPool, vazkii.botania.common.block.ModBlocks.fabulousPool,
                vazkii.botania.common.block.ModBlocks.alchemyCatalyst,
                vazkii.botania.common.block.ModBlocks.dilutedPool,
                vazkii.botania.common.block.ModBlocks.conjurationCatalyst);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.ModBlocks.mechanicalRunicAltar, vazkii.botania.common.block.ModBlocks.runeAltar,
                Ingredient.fromItems(vazkii.botania.common.item.ModItems.runeLust,
                        vazkii.botania.common.item.ModItems.runeGluttony,
                        vazkii.botania.common.item.ModItems.runeGreed,
                        vazkii.botania.common.item.ModItems.runeSloth,
                        vazkii.botania.common.item.ModItems.runeWrath,
                        vazkii.botania.common.item.ModItems.runeEnvy,
                        vazkii.botania.common.item.ModItems.runePride));

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.ModBlocks.mechanicalBrewery, vazkii.botania.common.block.ModBlocks.brewery,
                vazkii.botania.common.item.ModItems.flask, Items.BLAZE_ROD, vazkii.botania.common.item.ModItems.flask);

        this.defaultMachine(consumer, de.melanx.botanicalmachinery.ModBlocks.mechanicalApothecary, vazkii.botania.common.block.ModBlocks.defaultAltar,
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
                .addCriterion("has_item", hasItem(input))
                .build(consumer, new ResourceLocation(BotanicalMachinery.getInstance().modid, "compress/" + output.asItem().getRegistryName().getPath()));
    }

    private void decompress(Consumer<IFinishedRecipe> consumer, IItemProvider output, Item input) {
        //noinspection ConstantConditions
        ShapelessRecipeBuilder.shapelessRecipe(output, 9)
                .addIngredient(input)
                .addCriterion("has_item", hasItem(input))
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
                .key('a', vazkii.botania.common.item.ModItems.auraRingGreater)
                .key('s', special1)
                .key('x', special2)
                .key('y', special3)
                .key('z', special4)
                .patternLine("eye")
                .patternLine("xaz")
                .patternLine("ese")
                .addCriterion("has_item", hasItem(special1))
                .build(consumer);
    }

    private void registerExtraRecipes(Consumer<IFinishedRecipe> consumer) {
        this.shaped(vazkii.botania.common.item.ModItems.manaTablet)
                .key('P', Ingredient.fromItems(de.melanx.botanicalmachinery.ModItems.manaEmerald))
                .key('S', ModTags.Items.LIVINGROCK)
                .patternLine("SSS")
                .patternLine("SPS")
                .patternLine("SSS")
                .addCriterion("has_item", hasItem(de.melanx.botanicalmachinery.ModItems.manaEmerald))
                .build(consumer, this.changedBotaniaLoc(vazkii.botania.common.item.ModItems.manaTablet));

        this.shaped(vazkii.botania.common.block.ModBlocks.runeAltar)
                .key('P', de.melanx.botanicalmachinery.ModItems.manaEmerald)
                .key('S', ModTags.Items.LIVINGROCK)
                .patternLine("SSS")
                .patternLine("SPS")
                .addCriterion("has_item", hasItem(de.melanx.botanicalmachinery.ModItems.manaEmerald))
                .build(consumer, this.changedBotaniaLoc(vazkii.botania.common.block.ModBlocks.runeAltar.asItem()));
    }

    private ResourceLocation changedBotaniaLoc(Item item) {
        @SuppressWarnings("ConstantConditions")
        String name = item.asItem().getRegistryName().getPath();
        return new ResourceLocation(BotanicalMachinery.getInstance().modid, "botania/" + name);
    }
}
