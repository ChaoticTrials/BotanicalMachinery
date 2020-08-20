package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModSubtiles;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Recipes extends net.minecraft.data.RecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {

        this.shaped(Registration.BLOCK_MANA_BATTERY.get())
                .key('d', ModTags.Items.GEMS_DRAGONSTONE)
                .key('g', ModItems.gaiaIngot)
                .key('r', ModItems.manaRingGreater)
                .key('b', ModBlocks.manaDiamondBlock)
                .patternLine("dgd")
                .patternLine("grg")
                .patternLine("dbd")
                .addCriterion("has_item", this.hasItem(ModItems.manaRingGreater))
                .build(consumer);

        this.shaped(Registration.BLOCK_MECHANICAL_DAISY.get())
                .key('e', ModTags.Items.INGOTS_ELEMENTIUM)
                .key('d', ModSubtiles.pureDaisyFloating)
                .patternLine(" d ")
                .patternLine("eee")
                .addCriterion("has_item", this.hasItem(ModSubtiles.pureDaisyFloating))
                .build(consumer);

        this.defaultMachine(consumer, Registration.BLOCK_ALFHEIM_MARKET.get(), ModBlocks.alfPortal,
                ModBlocks.livingwoodGlimmering);

        this.defaultMachine(consumer, Registration.BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), ModBlocks.terraPlate,
                Ingredient.fromTag(ModTags.Items.GEMS_MANA_DIAMOND),
                Ingredient.fromTag(ModTags.Items.INGOTS_MANASTEEL),
                Ingredient.fromItems(ModItems.manaPearl));

        this.defaultMachine(consumer, Registration.BLOCK_MECHANICAL_MANA_POOL.get(), ModBlocks.fabulousPool,
                ModBlocks.dilutedPool);

        this.defaultMachine(consumer, Registration.BLOCK_MECHANICAL_RUNIC_ALTAR.get(), ModBlocks.runeAltar,
                Ingredient.fromTag(ModTags.Items.RUNES));

        this.defaultMachine(consumer, Registration.BLOCK_MECHANICAL_BREWERY.get(), ModBlocks.brewery,
                ModItems.vial, Items.BLAZE_POWDER, ModItems.vial);
    }

    private ShapedRecipeBuilder shaped(IItemProvider result) {
        //noinspection ConstantConditions
        return ShapedRecipeBuilder.shapedRecipe(result).setGroup(BotanicalMachinery.MODID + ":" + result.asItem().getRegistryName().getPath());
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
                .key('a', ModItems.auraRing)
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
}
