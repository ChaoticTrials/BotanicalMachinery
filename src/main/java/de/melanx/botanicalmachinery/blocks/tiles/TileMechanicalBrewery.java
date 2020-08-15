package de.melanx.botanicalmachinery.blocks.tiles;

import com.sun.org.apache.regexp.internal.ReaderCharacterIterator;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.IntStream;

public class TileMechanicalBrewery extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(8);
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);
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
        if (Arrays.stream(this.inventory.getOutputSlots()).anyMatch(x -> x == slot)) return false;
        if (slot == 0) return stack.getTag() != null ? !stack.getTag().contains("brewKey") : RecipeHelper.brewContainer.contains(stack.getItem());
        return (Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot)) || RecipeHelper.brewIngredients.contains(stack.getItem());
    }
}
