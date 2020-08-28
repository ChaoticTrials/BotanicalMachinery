package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.BlockManaBattery;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.mana.IManaItem;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TileManaBattery extends TileBase {

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(2, slot -> this.sendPacket = true, this::isValidStack);

    public TileManaBattery() {
        super(Registration.TILE_MANA_BATTERY.get(), 10_000_000);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (stack.getItem() instanceof IManaItem) {
            IManaItem item = (IManaItem) stack.getItem();
            if (slot == 0 && item.getMana(stack) >= item.getMaxMana(stack)) return false;
            if (slot == 1 && item.getMana(stack) <= 0) return false;
        }
        return stack.getItem() instanceof IManaItem;
    }

    @Override
    public int getCurrentMana() {
        return ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.CREATIVE ? this.getManaCap() : this.mana;
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
                    int maxManaValue = ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.NORMAL ? 1000 : Integer.MAX_VALUE;
                    int manaValue = Math.min(maxManaValue, Math.min(this.getCurrentMana(), manaItem.getMaxMana(minus) - manaItem.getMana(minus)));
                    manaItem.addMana(minus, manaValue);
                    this.receiveMana(-manaValue);
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if (!plus.isEmpty()) {
                if (plus.getItem() instanceof IManaItem) {
                    IManaItem manaItem = (IManaItem) plus.getItem();
                    int maxManaValue = ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.NORMAL ? 1000 : Integer.MAX_VALUE;
                    int manaValue = Math.min(maxManaValue, Math.min(this.getManaCap() - this.getCurrentMana(), manaItem.getMana(plus)));
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
                        int maxManaValue = ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.NORMAL ? 5000 : Integer.MAX_VALUE;
                        int manaValue = Math.min(maxManaValue, Math.min(this.getCurrentMana(), offsetTile.getManaCap() - offsetTile.getCurrentMana()));
                        this.receiveMana(-manaValue);
                        offsetTile.receiveMana(manaValue);
                        this.markDirty();
                        this.markDispatchable();
                    }
                }
            }
        }
    }

    @Override
    protected LazyOptional<IItemHandlerModifiable> createHandler(Supplier<IItemHandlerModifiable> inventory) {
        return ItemStackHandlerWrapper.createLazy(inventory, slot -> {
            ItemStack minus = inventory.get().getStackInSlot(0);
            ItemStack plus = inventory.get().getStackInSlot(1);
            if (slot == 0 && minus.getItem() instanceof IManaItem) {
                IManaItem manaItem = (IManaItem) minus.getItem();
                return manaItem.getMana(minus) >= manaItem.getMaxMana(minus);
            } else if (slot == 1 && plus.getItem() instanceof IManaItem) {
                IManaItem manaItem = (IManaItem) plus.getItem();
                return manaItem.getMana(plus) <= 0;
            }
            return true;
        }, null);
    }
}
