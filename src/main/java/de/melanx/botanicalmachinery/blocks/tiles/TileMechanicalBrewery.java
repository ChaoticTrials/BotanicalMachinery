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
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.api.recipe.IBrewRecipe;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.IntStream;

public class TileMechanicalBrewery extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(8);
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);
    private IBrewRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private int workingDuration = -1;
    private boolean update;

    private static final String TAG_PROGRESS = "progress";
    private static final String TAG_WORKING_DURATION = "workingDuration";

    public TileMechanicalBrewery() {
        super(Registration.TILE_MECHANICAL_BREWERY.get(), 100_000);
        this.inventory.setInputSlots(IntStream.range(0, 8).toArray());
        this.inventory.setOutputSlots(8);
        this.inventory.setSlotValidator(this::canInsertStack);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.getTag() != null ? !stack.getTag().contains("brewKey") : RecipeHelper.brewContainer.contains(stack.getItem());
        return (Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot)) || RecipeHelper.brewIngredients.contains(stack.getItem());
    }

    private void updateRecipe() {
        if (world != null && !world.isRemote) {
            if (this.inventory.getStackInSlot(0).isEmpty()) {
                this.recipe = null;
                return;
            }
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.remove(7);
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

            for (IBrewRecipe recipe : RecipeHelper.brewRecipes) {
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

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);
        cmp.putInt(TAG_PROGRESS, this.progress);
        cmp.putInt(TAG_WORKING_DURATION, this.workingDuration);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInt(TAG_PROGRESS);
        this.workingDuration = cmp.getInt(TAG_WORKING_DURATION);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.initDone) {
            this.update = true;
            this.initDone = true;
        }
        this.updateRecipe(); // todo remove
        boolean done = false;
        if (this.recipe != null) {
            ItemStack output = this.recipe.getOutput(this.inventory.getStackInSlot(0)).copy();
            ItemStack currentOutput = this.inventory.getStackInSlot(7);
            if (!output.isEmpty() && (currentOutput.isEmpty() || (ItemStack.areItemStacksEqual(output, currentOutput) && currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize()))) {
                int recipeCost = this.getManaCost();
                this.workingDuration = recipeCost / 100;
                if (this.getCurrentMana() >= recipeCost || this.progress > 0 && this.progress <= this.workingDuration) {
                    ++this.progress;
                    this.receiveMana(-(recipeCost / this.workingDuration));
                    if (this.progress >= this.workingDuration) {
                        if (currentOutput.isEmpty()) {
                            this.inventory.setStackInSlot(7, output);
                        } else {
                            currentOutput.setCount(currentOutput.getCount() + output.getCount());
                        }
                        this.inventory.getStackInSlot(0).shrink(1);
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
        if (this.update) {
            this.updateRecipe();
            this.update = false;
        }
        if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
            this.progress = 0;
            this.workingDuration = -1;
            this.markDirty();
            this.markDispatchable();
        }
    }

    @Override
    public boolean hasValidRecipe() {
        return true;
    }

    public int getManaCost() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (recipe == null || stack.isEmpty() || !(stack.getItem() instanceof IBrewContainer)) {
            return 0;
        }
        IBrewContainer container = (IBrewContainer) stack.getItem();
        return container.getManaCost(recipe.getBrew(), stack);
    }

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap, Direction direction) {
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap);
    }
}
