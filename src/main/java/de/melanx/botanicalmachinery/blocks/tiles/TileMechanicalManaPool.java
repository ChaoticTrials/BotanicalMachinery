package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileMechanicalManaPool extends TileBase {
    public static final List<Item> CATALYSTS = Arrays.asList(ModBlocks.alchemyCatalyst.asItem(), ModBlocks.conjurationCatalyst.asItem());
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(3, this::onSlotChanged, this::isValidStack);
    public boolean validRecipe = true;

    public TileMechanicalManaPool() {
        super(Registration.TILE_MECHANICAL_MANA_POOL.get(), 100_000);
        this.inventory.addSlotLimit(0, 1);
        this.inventory.setOutputSlots(2);
    }

    public IManaInfusionRecipe getMatchingRecipe(@Nonnull ItemStack stack, @Nonnull ItemStack cat) {
        List<IManaInfusionRecipe> matchingNonCatRecipes = new ArrayList<>();
        List<IManaInfusionRecipe> matchingCatRecipes = new ArrayList<>();

        //noinspection ConstantConditions
        for (IManaInfusionRecipe recipe : TilePool.manaInfusionRecipes(this.world.getRecipeManager())) {
            if (recipe.matches(stack)) {
                if (recipe.getCatalyst() == null) {
                    matchingNonCatRecipes.add(recipe);
                } else if (recipe.getCatalyst().getBlock().asItem() == cat.getItem()) {
                    matchingCatRecipes.add(recipe);
                }
            }
        }

        // Recipes with matching catalyst take priority above recipes with no catalyst specified
        return !matchingCatRecipes.isEmpty() ? matchingCatRecipes.get(0) : !matchingNonCatRecipes.isEmpty() ? matchingNonCatRecipes.get(0) : null;
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    private void onSlotChanged(int slot) {
        if (slot == 1) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            IManaInfusionRecipe recipe = this.getMatchingRecipe(stack, cat);
            if (recipe != null) {
                this.validRecipe = recipe.getManaToConsume() <= this.getCurrentMana();
            } else {
                this.validRecipe = stack.isEmpty();
            }
        }
        this.sendPacket = true;
        this.markDirty();
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot == 0) return CATALYSTS.contains(stack.getItem());
        if (slot == 1) return RecipeHelper.isItemValid(this.world, ModRecipeTypes.MANA_INFUSION_TYPE, stack);
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!ManaNetworkHandler.instance.isPoolIn(this) && !this.isRemoved()) {
            ManaNetworkEvent.addCollector(this);
        }

        if (this.world != null) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            IManaInfusionRecipe recipe = this.getMatchingRecipe(stack, cat);
            if (!this.world.isRemote) {
                if (recipe != null) {
                    int mana = recipe.getManaToConsume();
                    if (this.getCurrentMana() >= mana && (this.getInventory().getStackInSlot(2).isEmpty() ||
                            (recipe.getRecipeOutput().getItem() == this.getInventory().getStackInSlot(2).getItem() &&
                                    this.getInventory().getStackInSlot(2).getMaxStackSize() > this.getInventory().getStackInSlot(2).getCount()))) {
                        this.receiveMana(-mana);
                        stack.shrink(1);

                        ItemStack output = recipe.getRecipeOutput().copy();
                        this.inventory.getUnrestricted().insertItem(2, output, false);
                        this.markDirty();
                    }
                }
            }
        }
    }

    @Override
    public boolean hasValidRecipe() {
        return this.validRecipe;
    }
}
