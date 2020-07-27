package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import vazkii.botania.api.recipe.IRuneAltarRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.material.ItemRune;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class TileMechanicalRunicAltar extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(33, this.onContentsChanged());
    private IRuneAltarRecipe recipe = null;
    private boolean validRecipe;
    private NonNullList<ItemStack> input;

    public TileMechanicalRunicAltar() {
        super(Registration.TILE_MECHANICAL_RUNIC_ALTAR.get(), 10_000_000);
        this.input = NonNullList.withSize(16, ItemStack.EMPTY);
        this.inventory.setOutputSlots(IntStream.range(17, 33).toArray());
        this.inventory.setSlotValidator(this::canInsertStack);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        if (slot == 0) return stack.getItem() == ModBlocks.livingrock.asItem();
        else if (slot <= 16) return RecipeHelper.runeAltarIngredients.contains(stack.getItem());
        return true;
    }

    private IRuneAltarRecipe updateRecipe() {
        if (world != null && !world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.subList(17, stacks.size() - 1).clear();
            stacks.remove(0);
            Map<Item, Integer> items = new HashMap<>();
            stacks.removeIf(stack -> stack.getItem() == Blocks.AIR.asItem());
            stacks.forEach(stack -> {
                Item item = stack.getItem();
                if (!items.containsKey(item)) {
                    items.put(item, stack.getCount());
                } else {
                    int prevCount = items.get(item);
                    items.replace(item, prevCount, prevCount + stack.getCount());
                }
            });
            items.forEach((name, count) -> BotanicalMachinery.LOGGER.debug(count + "x " + name)); // todo remove after debugging

            for (IRuneAltarRecipe recipe : RecipeHelper.runeAltarRecipes) {
                List<Ingredient> recipeIngredients = new ArrayList<>(recipe.getIngredients());

                for (int i = 0; i < stacks.size(); i++) {
                    ItemStack input = stacks.get(i);

                    int stackIndex = -1;

                    for (int j = 0; j < recipeIngredients.size(); j++) {
                        Ingredient ingredient = recipeIngredients.get(j);
                        if (ingredient.test(input)) {
                            stackIndex = j;
                            break;
                        }
                    }

                    if (stackIndex != -1) {
                        recipeIngredients.remove(stackIndex);
                    }
                }
                if (recipeIngredients.isEmpty() && this.inventory.getStackInSlot(0) != ItemStack.EMPTY) {
                    return this.recipe = recipe;
                }
            }
        }
        this.recipe = null;
        return null;
//        return this.recipe;
    }

    private Function<Integer, Void> onContentsChanged() {
        return slot -> {
            this.updateRecipe();
            if (this.recipe != null) {
//                    validRecipe = recipe.getManaToConsume() <= getCurrentMana();
                System.out.println("recipe for: " + this.recipe.getRecipeOutput());
            } else {
//                    validRecipe = stack.isEmpty();
                System.out.println("no recipe found");
            }
            markDirty();
            return null;
        };
    }

    @Override
    public boolean hasValidRecipe() {
        return true; //this.validRecipe;
    }

    @Override
    public void tick() {
        super.tick();
        if (world != null && !world.isRemote) {
            if (this.recipe != null) {
                for (Ingredient ingredient : this.recipe.getIngredients()) {
                    for (ItemStack stack : this.inventory.getStacks()) {
                        if (ingredient.test(stack)) {
                            if (stack.getItem() instanceof ItemRune) {
                                ItemStack rune = stack.copy();
                                rune.setCount(1);
                                this.putIntoOutput(rune);
                            }
                            stack.shrink(1);
                            break;
                        }
                    }
                }
                this.inventory.getStackInSlot(0).shrink(1);
                this.putIntoOutput(this.recipe.getRecipeOutput().copy());
                this.updateRecipe();
            }
        }
    }

    private void putIntoOutput(ItemStack stack) {
        for (int i : this.inventory.getOutputSlots()) {
            if (stack.isEmpty()) break;
            ItemStack slotStack = this.inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                this.inventory.insertItemSuper(i, stack.copy(), false);
                break;
            } else if ((slotStack.getItem() == stack.getItem() && slotStack.getCount() < slotStack.getMaxStackSize())) {
                ItemStack left = this.inventory.insertItemSuper(i, stack, false);
                if (left != ItemStack.EMPTY) stack = left;
                else break;
            }
        }
    }
}
