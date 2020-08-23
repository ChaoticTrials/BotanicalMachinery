package de.melanx.botanicalmachinery.helper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecipeHelper {

    /**
     * @param world      {@link World} to get the {@link net.minecraft.item.crafting.RecipeManager} from
     * @param recipeType {@link IRecipeType} to filter which recipe type will be checked
     * @param input      {@link ItemStack} which will be checked to fit
     * @return If the input is in any recipe
     */
    public static <X extends IRecipe<?>> boolean isItemValid(@Nullable World world, IRecipeType<X> recipeType, ItemStack input) {
        if (world == null) return false;
        Collection<IRecipe<?>> recipes = world.getRecipeManager().getRecipes();
        for (IRecipe<?> recipe : recipes) {
            if (recipe.getType() == recipeType) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (ItemStack stack : ingredient.getMatchingStacks()) {
                        if (stack.getItem() == input.getItem())
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if stacks are ingredients for the recipe
     *
     * @param stacks {@link List} of {@link ItemStack} which includes the stacks to be checked
     * @param items  {@link Map} with {@link Item} item and {@link Integer} amount of all items needed for the recipe
     * @param recipe The {@link IRecipe} to be checked
     * @return If all items are contained in the stacks
     */
    public static boolean checkIngredients(List<ItemStack> stacks, Map<Item, Integer> items, IRecipe<?> recipe) {
        Map<Ingredient, Integer> recipeIngredients = new LinkedHashMap<>();
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient ingredient = recipe.getIngredients().get(i);
            boolean done = false;
            for (Ingredient ingredient1 : recipeIngredients.keySet()) {
                if (ingredient.serialize().equals(ingredient1.serialize())) {
                    recipeIngredients.replace(ingredient1, recipeIngredients.get(ingredient1) + 1);
                    done = true;
                    break;
                }
            }
            if (!done) recipeIngredients.put(ingredient, 1);
        }

        for (ItemStack input : stacks) {
            Ingredient remove = RecipeHelper.getMatchingIngredient(recipeIngredients, items, input);
            if (remove != null) {
                recipeIngredients.remove(remove);
            }
        }
        return recipeIngredients.isEmpty();
    }

    /**
     * Used to find the matching ingredient to an inventories {@link ItemStack}
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
