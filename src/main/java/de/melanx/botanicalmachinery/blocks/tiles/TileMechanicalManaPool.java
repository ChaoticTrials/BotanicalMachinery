package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalManaPool extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(3, (slot) -> {
        if (slot == 1) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            IManaInfusionRecipe recipe = getMatchingRecipe(stack, cat);
            if (recipe != null) {
                validRecipe = recipe.getManaToConsume() <= getCurrentMana();
            } else {
                validRecipe = stack.isEmpty();
            }
        }
        markDirty();
        return null;
    });
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory, this::canInsertStack, null);
    public boolean validRecipe = true;

    public TileMechanicalManaPool() {
        super(Registration.TILE_MECHANICAL_MANA_POOL.get(), 10_000_000);
        this.inventory.addSlotLimit(0, 1);
        this.inventory.setOutputSlots(2);
    }

    public IManaInfusionRecipe getMatchingRecipe(@Nonnull ItemStack stack, @Nonnull ItemStack cat) {
        List<IManaInfusionRecipe> matchingNonCatRecipes = new ArrayList<>();
        List<IManaInfusionRecipe> matchingCatRecipes = new ArrayList<>();

        for (IManaInfusionRecipe recipe : TilePool.manaInfusionRecipes(world.getRecipeManager())) {
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

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        if (slot == 1) return true;
        if (slot == 0) return BotanicalMachinery.catalysts.contains(stack.getItem());
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!ManaNetworkHandler.instance.isPoolIn(this) && !isRemoved()) {
            ManaNetworkEvent.addCollector(this);
        }

        if (world != null) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            IManaInfusionRecipe recipe = getMatchingRecipe(stack, cat);
            if (!world.isRemote) {
                if (recipe != null) {
                    int mana = recipe.getManaToConsume();
                    if (getCurrentMana() >= mana && (this.getInventory().getStackInSlot(2).isEmpty() ||
                            (recipe.getRecipeOutput().getItem() == this.getInventory().getStackInSlot(2).getItem() &&
                                    this.getInventory().getStackInSlot(2).getMaxStackSize() > this.getInventory().getStackInSlot(2).getCount()))) {
                        receiveMana(-mana);
                        stack.shrink(1);

                        ItemStack output = recipe.getRecipeOutput().copy();
                        this.inventory.insertItemSuper(2, output, false);
                        markDirty();
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.removed && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean hasValidRecipe() {
        return validRecipe;
    }
}
