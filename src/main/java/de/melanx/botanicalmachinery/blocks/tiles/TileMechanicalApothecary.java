package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.recipe.IPetalRecipe;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TileMechanicalApothecary extends TileMod implements ITickableTileEntity {

    private static final int WORKING_DURATION = 20;
    public static final int FLUID_CAPACITY = 8000;
    public static final String TAG_INV = "inv";
    public static final String TAG_FLUID = "fluid";
    public static final String TAG_PROGRESS = "progress";

    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.createLazy(this::getInventory);
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(21, slot -> this.update = true, this::isValidStack);
    private final ModdedFluidTank fluidInventory = new ModdedFluidTank(FLUID_CAPACITY, fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER));
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.fluidInventory);
    private IPetalRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;
    private boolean sendPacket;

    public TileMechanicalApothecary() {
        super(Registration.TILE_MECHANICAL_APOTHECARY.get());
    }

    @Nonnull
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Nonnull
    public FluidTank getFluidInventory() {
        return this.fluidInventory;
    }

    public boolean isValidStack(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void tick() {
        if (this.sendPacket) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            this.sendPacket = false;
        }
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        cmp.put(TAG_INV, this.getInventory().serializeNBT());
        final CompoundNBT tankTag = new CompoundNBT();
        this.getFluidInventory().getFluid().writeToNBT(tankTag);
        cmp.put(TAG_FLUID, tankTag);
        cmp.putInt(TAG_PROGRESS, this.progress);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        this.getInventory().deserializeNBT(cmp.getCompound(TAG_INV));
        this.fluidInventory.setFluid(FluidStack.loadFluidStackFromNBT(cmp.getCompound(TAG_FLUID)));
        this.progress = cmp.getInt(TAG_PROGRESS);
    }

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap, @Nullable Direction side) {
        if (!this.removed && (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return this.handler.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private class ModdedFluidTank extends FluidTank {
        public ModdedFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            TileMechanicalApothecary.this.sendPacket = true;
            TileMechanicalApothecary.this.update = true;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}
