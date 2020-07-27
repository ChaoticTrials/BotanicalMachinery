package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import vazkii.botania.api.recipe.IRuneAltarRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.Optional;
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
        this.inventory.setOutputSlots(IntStream.range(17, 34).toArray());
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
        else if (slot <= 16) return stack.getItem() != ModBlocks.livingrock.asItem();
        return true;
    }

    private IRuneAltarRecipe getRecipe() {
        if (!world.isRemote) {
            RecipeManager manager = this.world.getRecipeManager();
            Optional<IRuneAltarRecipe> r = manager.getRecipe(ModRecipeTypes.RUNE_TYPE, this.getRecipeWrapper(), this.world);
            this.recipe = r.orElse(null);
        }
        return this.recipe;
    }

    private Function<Integer, Void> onContentsChanged() {
        return slot -> {
            IRuneAltarRecipe recipe = getRecipe();
            if (recipe != null) {
//                    validRecipe = recipe.getManaToConsume() <= getCurrentMana();
                System.out.println("recipe for: " + recipe.getRecipeOutput());
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
        if (this.getRecipe() != null)
            System.out.println("recipe: " + this.getRecipe().getRecipeOutput());
    }
}
