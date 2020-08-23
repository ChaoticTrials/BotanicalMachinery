package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.api.recipe.IBrewRecipe;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TileMechanicalBrewery extends TileBase {
    public static final List<Item> BREW_CONTAINER = Arrays.asList(ModItems.vial.asItem(), ModItems.flask.asItem(), ModItems.incenseStick.asItem(), ModItems.bloodPendant.asItem());
    public static final String TAG_PROGRESS = "progress";
    public static final String TAG_WORKING_DURATION = "workingDuration";

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(8, slot -> {this.update = true; this.sendPacket = true;}, this::isValidStack);
    private IBrewRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private int workingDuration = -1;
    private boolean update;

    public TileMechanicalBrewery() {
        super(Registration.TILE_MECHANICAL_BREWERY.get(), 100_000);
        this.inventory.setInputSlots(IntStream.range(0, 7).toArray());
        this.inventory.setOutputSlots(7);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.getTag() != null ? !stack.getTag().contains("brewKey") : BREW_CONTAINER.contains(stack.getItem());
        return (Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot)) || RecipeHelper.isItemValid(this.world, ModRecipeTypes.BREW_TYPE, stack);
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            if (this.inventory.getStackInSlot(0).isEmpty()) {
                this.recipe = null;
                return;
            }
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            RecipeHelper.removeFromList(stacks, new int[]{0, 7});
            Map<Item, Integer> items = RecipeHelper.getInvItems(stacks);

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IBrewRecipe) {
                    if (RecipeHelper.checkIngredients(stacks, items, recipe)) {
                        this.recipe = (IBrewRecipe) recipe;
                        return;
                    }
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
        if (this.world != null && !this.world.isRemote) {
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
                        }
                    }
                    this.markDirty();
                    this.markDispatchable();
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
    }

    @Override
    public boolean hasValidRecipe() {
        return true;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getWorkingDuration() {
        return this.workingDuration;
    }

    public int getManaCost() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (this.recipe == null || stack.isEmpty() || !(stack.getItem() instanceof IBrewContainer)) {
            return 0;
        }
        IBrewContainer container = (IBrewContainer) stack.getItem();
        return container.getManaCost(this.recipe.getBrew(), stack);
    }
}
