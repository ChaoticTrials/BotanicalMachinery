package de.melanx.botanicalmachinery.helper;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.api.recipe.IRuneAltarRecipe;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BotanicalMachinery.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecipeHelper {
    public static final List<Item> manaPoolCatalysts = new ArrayList<>();
    public static final List<Item> manaPoolIngredients = new ArrayList<>();
    public static final List<Item> runeAltarIngredients = new ArrayList<>();
    public static final List<IRuneAltarRecipe> runeAltarRecipes = new ArrayList<>();

    @SubscribeEvent
    public static void onRecipesUpdated(final RecipesUpdatedEvent event) {
        manaPoolCatalysts.clear();
        manaPoolIngredients.clear();
        runeAltarIngredients.clear();
        runeAltarRecipes.clear();
        for (IRecipe<?> r : event.getRecipeManager().getRecipes()) {
            if (r instanceof IManaInfusionRecipe) {
                IManaInfusionRecipe recipe = (IManaInfusionRecipe) r;
                if (recipe.getCatalyst() != null) {
                    Item catalyst = recipe.getCatalyst().getBlock().asItem();
                    if (!manaPoolCatalysts.contains(catalyst))
                        manaPoolCatalysts.add(catalyst);
                }
                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (ItemStack stack : ingredient.getMatchingStacks()) {
                        if (!manaPoolIngredients.contains(stack.getItem()))
                            manaPoolIngredients.add(stack.getItem());
                    }
                }
            } else if (r instanceof IRuneAltarRecipe) {
                IRuneAltarRecipe recipe = (IRuneAltarRecipe) r;
                runeAltarRecipes.add(recipe);
                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (ItemStack stack : ingredient.getMatchingStacks()) {
                        if (!runeAltarIngredients.contains(stack.getItem())) {
                            runeAltarIngredients.add(stack.getItem());
                        }
                    }
                }
            }
        }
    }
}
