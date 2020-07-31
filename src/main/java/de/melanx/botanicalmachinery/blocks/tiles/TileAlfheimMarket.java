package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.recipe.IElvenTradeRecipe;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class TileAlfheimMarket extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(5, this.onContentsChanged());
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);
    private IElvenTradeRecipe recipe = null;
    private boolean initDone;
    private final int workingDuration = 100;
    private int progress;
    private boolean update;

    public TileAlfheimMarket() {
        super(Registration.TILE_ALFHEIM_MARKET.get(), 500_000);
        this.inventory.setSlotValidator(this::canInsertStack);
        this.inventory.setInputSlots(IntStream.range(0, 4).toArray());
        this.inventory.setOutputSlots(4);
        this.update = true;
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        return RecipeHelper.elvenTradeIngredients.contains(stack.getItem());
    }

    private void updateRecipe() {
        if (world != null && !world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.remove(4);
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

            for (IElvenTradeRecipe recipe : RecipeHelper.elvenTradeRecipes) {
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
                if (recipeIngredients.isEmpty() && !this.inventory.getStackInSlot(0).isEmpty()) {
                    this.recipe = recipe;
                    return;
                }
            }
        }
        this.recipe = null;
    }

    private Function<Integer, Void> onContentsChanged() {
        return slot -> {
            this.update = true;
            return null;
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (world != null && !world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }

            if (this.update) {
                this.updateRecipe();
                this.update = false;
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

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap) {
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap);
    }
}
