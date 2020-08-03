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
import net.minecraft.nbt.CompoundNBT;
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
    public static final int WORKING_DURATION = 500;
    private static final int RECIPE_COST = 500;
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(5, this.onContentsChanged());
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);
    private IElvenTradeRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;

    private static final String TAG_PROGRESS = "progress";

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
        return Arrays.stream(this.inventory.getOutputSlots()).anyMatch(x -> x == slot) || RecipeHelper.elvenTradeIngredients.contains(stack.getItem());
    }

    private void updateRecipe() {
        if (world != null && !world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.remove(4);
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
                if (recipeIngredients.isEmpty()) {
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
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);
        cmp.putInt(TAG_PROGRESS, this.progress);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInt(TAG_PROGRESS);
    }

    @Override
    public void tick() {
        super.tick();
        if (world != null && !world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (recipe != null) {
                List<ItemStack> outputs = new ArrayList<>(this.recipe.getOutputs());
                if (outputs.size() == 1) {
                    if (this.inventory.insertItemSuper(4, outputs.get(0), true).isEmpty()) {
                        if (this.getCurrentMana() >= RECIPE_COST || this.progress > 0 && this.progress <= WORKING_DURATION) {
                            ++this.progress;
                            this.receiveMana(-(RECIPE_COST / WORKING_DURATION));
                            if (this.progress >= WORKING_DURATION) {
                                this.inventory.insertItemSuper(4, outputs.get(0).copy(), false);
                                for (Ingredient ingredient : this.recipe.getIngredients()) {
                                    for (ItemStack stack : this.inventory.getStacks()) {
                                        if (ingredient.test(stack)) {
                                            stack.shrink(1);
                                            break;
                                        }
                                    }
                                }
                                this.update = true;
                                done = true;
                                this.markDirty();
                                this.markDispatchable();
                            }
                        }
                    }
                }
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            }
        }
    }

    public int getProgress() {
        return this.progress;
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
