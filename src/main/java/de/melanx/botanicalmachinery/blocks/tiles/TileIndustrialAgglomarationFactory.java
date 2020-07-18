package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileIndustrialAgglomarationFactory extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4);
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);
    public TileIndustrialAgglomarationFactory() {
        super(Registration.TILE_INDUSTRIAL_AGGLOMARATION_FACTORY.get(), 10_000_000);
        this.inventory.setOutputSlots(3);
        this.inventory.setSlotValidator(this::canInsertStack);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        return (slot == 0 && ModTags.Items.INGOTS_MANASTEEL.contains(stack.getItem())) ||
                (slot == 1 && ModTags.Items.GEMS_MANA_DIAMOND.contains(stack.getItem())) ||
                (slot == 2 && ModItems.manaPearl == stack.getItem());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.removed && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap, side);
    }
}
