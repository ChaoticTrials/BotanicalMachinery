package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.mana.IManaItem;

import javax.annotation.Nonnull;

public class TileManaBattery extends TileBase {
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(2, null);
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);

    public TileManaBattery() {
        super(Registration.TILE_MANA_BATTERY.get(), 10_000_000);
        this.inventory.setSlotValidator(this::canInsertStack);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        return stack.getItem() instanceof IManaItem;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world != null && !this.world.isRemote) {
            ItemStack minus = this.inventory.getStackInSlot(0);
            ItemStack plus = this.inventory.getStackInSlot(1);
            if (!minus.isEmpty()) {
                if (minus.getItem() instanceof IManaItem) {
                    IManaItem manaItem = (IManaItem) minus.getItem();
                    int manaValue = Math.min(1000, Math.min(this.getCurrentMana(), manaItem.getMaxMana(minus) - manaItem.getMana(minus)));
                    manaItem.addMana(minus, manaValue);
                    this.receiveMana(-manaValue);
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if (!plus.isEmpty()) {
                if (plus.getItem() instanceof IManaItem) {
                    IManaItem manaItem = (IManaItem) plus.getItem();
                    int manaValue = Math.min(1000, Math.min(this.getManaCap() - this.getCurrentMana(), manaItem.getMana(plus)));
                    manaItem.addMana(plus, -manaValue);
                    this.receiveMana(manaValue);
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            for (Direction direction : Direction.values()) {
                TileEntity tile = this.world.getTileEntity(this.getPos().offset(direction));
                if (tile instanceof TileBase) {
                    TileBase offsetTile = (TileBase) tile;
                    if (offsetTile.getCurrentMana() < offsetTile.getManaCap()) {
                        int manaValue = Math.min(5000, Math.min(this.getCurrentMana(), offsetTile.getManaCap() - offsetTile.getCurrentMana()));
                        this.receiveMana(-manaValue);
                        offsetTile.receiveMana(manaValue);
                        this.markDirty();
                        this.markDispatchable();
                    }
                }
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
