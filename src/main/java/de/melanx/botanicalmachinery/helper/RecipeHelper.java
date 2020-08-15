package de.melanx.botanicalmachinery.helper;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.api.recipe.IBrewRecipe;
import vazkii.botania.api.recipe.IElvenTradeRecipe;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.api.recipe.IRuneAltarRecipe;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BotanicalMachinery.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecipeHelper {
    public static final List<Item> manaPoolCatalysts = new ArrayList<>();
    public static final List<Item> brewContainer = Arrays.asList(ModItems.vial.asItem(), ModItems.flask.asItem(), ModItems.incenseStick.asItem(), ModItems.bloodPendant.asItem());
    public static final List<Item> manaPoolIngredients = new ArrayList<>();
    public static final List<Item> runeAltarIngredients = new ArrayList<>();
    public static final List<Item> elvenTradeIngredients = new ArrayList<>();
    public static final List<Item> brewIngredients = new ArrayList<>();
    public static final List<IRuneAltarRecipe> runeAltarRecipes = new ArrayList<>();
    public static final List<IElvenTradeRecipe> elvenTradeRecipes = new ArrayList<>();
    public static final List<IBrewRecipe> brewRecipes = new ArrayList<>();

    @SubscribeEvent
    public static void onRecipesUpdated(final RecipesUpdatedEvent event) {
        manaPoolCatalysts.clear();
        manaPoolIngredients.clear();
        runeAltarIngredients.clear();
        elvenTradeIngredients.clear();
        runeAltarRecipes.clear();
        elvenTradeRecipes.clear();
        brewRecipes.clear();
        for (IRecipe<?> r : event.getRecipeManager().getRecipes()) {
            if (r instanceof IManaInfusionRecipe) {
                IManaInfusionRecipe recipe = (IManaInfusionRecipe) r;
                if (recipe.getCatalyst() != null) {
                    Item catalyst = recipe.getCatalyst().getBlock().asItem();
                    if (!manaPoolCatalysts.contains(catalyst))
                        manaPoolCatalysts.add(catalyst);
                }
                addIngredientsToList(recipe.getIngredients(), manaPoolIngredients);
            } else if (r instanceof IRuneAltarRecipe) {
                IRuneAltarRecipe recipe = (IRuneAltarRecipe) r;
                runeAltarRecipes.add(recipe);
                addIngredientsToList(recipe.getIngredients(), runeAltarIngredients);
            } else if (r instanceof IElvenTradeRecipe) {
                IElvenTradeRecipe recipe = (IElvenTradeRecipe) r;
                elvenTradeRecipes.add(recipe);
                addIngredientsToList(recipe.getIngredients(), elvenTradeIngredients);
            } else if (r instanceof IBrewRecipe) {
                IBrewRecipe recipe = (IBrewRecipe) r;
                brewRecipes.add(recipe);
                addIngredientsToList(recipe.getIngredients(), brewIngredients);
            }
        }
    }

    private static void addIngredientsToList(NonNullList<Ingredient> ingredients, List<Item> list) {
        for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (!list.contains(stack.getItem())) {
                    list.add(stack.getItem());
                }
            }
        }
    }

    /**
     * Used to find the matching ingredient to an inventorys {@link ItemStack}
     *
     * @param ingredients {@link Map} with the {@link Ingredient} ingredient and {@link Integer} amount to search the matching one
     * @param items       {@link Map} with {@link Item} item and {@link Integer} amount of all items to be checked
     * @param input       specific {@link ItemStack} to be tested
     * @return matching {@link Ingredient}
     */
    @Nullable
    public static Ingredient getMatchingIngredient(Map<Ingredient, Integer> ingredients, Map<Item, Integer> items, ItemStack input) {
        for (Map.Entry<Ingredient, Integer> entry : ingredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int count = entry.getValue();
            if (ingredient.test(input)) {

                for (Map.Entry<Item, Integer> itemEntry : items.entrySet()) {
                    Item item = itemEntry.getKey();
                    int itemCount = itemEntry.getValue();
                    for (Ingredient.IItemList iItemList : ingredient.acceptedItems) {
                        for (ItemStack stack : iItemList.getStacks()) {
                            if (stack.getItem() == item) {
                                if (itemCount >= count) {
                                    return ingredient;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
