package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class TileIndustrialAgglomarationFactory extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4);
    public TileIndustrialAgglomarationFactory() {
        super(Registration.TILE_INDUSTRIAL_AGGLOMARATION_FACTORY.get(), 10_000_000);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        return true;
    }
}
