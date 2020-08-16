package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.recipe.IPureDaisyRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalDaisy extends TileMod implements ITickableTileEntity {

    private int ticksToNextUpdate = 5;
    // Negative value = recipe completed
    private int[] workingTicks = new int[8];
    private final InventoryHandler inventory = new InventoryHandler();

    private final LazyOptional<IItemHandlerModifiable> lazyInventory = ItemStackHandlerWrapper.create(inventory);

    // The canExtract function makes it so that hoppers can only extract items when the recipe is done.
    private final LazyOptional<IItemHandlerModifiable> hopperInventory = ItemStackHandlerWrapper.create(inventory, slot -> workingTicks[slot] < 0, null);

    public TileMechanicalDaisy() {
        super(Registration.TILE_MECHANICAL_DAISY.get());
    }

    @Override
    public void tick() {
        boolean hasSpawnedParticles = false;
        for (int i = 0; i < 8; i++) {
            IPureDaisyRecipe recipe = getRecipe(i);
            if (recipe != null) {
                //noinspection ConstantConditions
                if (!world.isRemote) {
                    if (workingTicks[i] >= recipe.getTime()) {
                        BlockState state = recipe.getOutputState();
                        if (state.getBlock().asItem() != Items.AIR) {
                            //noinspection deprecation
                            inventory.setStackInSlot(i, state.getBlock().getItem(world, pos, state));
                        } else if (state.getFluidState().getFluid().getFluid() != Fluids.EMPTY) {
                            inventory.setStackInSlot(i, new FluidStack(state.getFluidState().getFluid(), 1000));
                        }
                        workingTicks[i] = -1;
                    } else {
                        workingTicks[i] += 1;
                    }
                } else if (!hasSpawnedParticles) {
                    hasSpawnedParticles = true;
                    double x = pos.getX() + Math.random();
                    double y = pos.getY() + Math.random() + 0.5D;
                    double z = pos.getZ() + Math.random();
                    WispParticleData data = WispParticleData.wisp((float)Math.random() / 2.0F, 1.0F, 1.0F, 1.0F);
                    world.addParticle(data, x, y, z, 0.0D, 0.0D, 0.0D);
                }
            } else {
                if (workingTicks[i] < 0 && !inventory.getStackInSlot(i).isEmpty()) {
                    workingTicks[i] = -1;
                } else {
                    workingTicks[i] = 0;
                }
            }
        }
        //noinspection ConstantConditions
        if (!world.isRemote) {
            if (ticksToNextUpdate <= 0) {
                ticksToNextUpdate = 5;
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            } else {
                ticksToNextUpdate -= 1;
            }
        }
    }

    @Nullable
    private IPureDaisyRecipe getRecipe(int slot) {
        BlockState state = getState(slot);
        if (state == null)
            return null;
        return getRecipe(state);
    }

    @Nullable
    public BlockState getState(int slot) {
        BlockState state = null;

        ItemStack stack = inventory.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof BlockItem) {
                state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            }
        } else {
            FluidStack fluid = inventory.getFluidInTank(slot);
            if (!fluid.isEmpty()) {
                state = fluid.getFluid().getDefaultState().getBlockState();
            }
        }

        return state;
    }

    @Nullable
    public BlockState getState(ItemStack stack) {
        BlockState state = null;

        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof BlockItem) {
                state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            }
        }

        return state;
    }

    @Nullable
    public IPureDaisyRecipe getRecipe(BlockState state) {
        if (world == null)
            return null;

        for (IRecipe<?> genericRecipe : this.world.getRecipeManager().getRecipes(ModRecipeTypes.PURE_DAISY_TYPE).values()) {
            if (genericRecipe instanceof IPureDaisyRecipe) {
                IPureDaisyRecipe recipe = (IPureDaisyRecipe) genericRecipe;
                if (recipe.matches(world, pos, null, state)) {
                    return recipe;
                }
            }
        }

        return null;
    }

    public void writePacketNBT(CompoundNBT tag) {
        tag.put("inventory", inventory.serializeNBT());
        tag.putIntArray("workingTicks", workingTicks);
    }

    public void readPacketNBT(CompoundNBT tag) {
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }
        if (tag.contains("workingTicks")) {
            workingTicks = tag.getIntArray("workingTicks");
        }
    }

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap, @Nullable Direction side) {
        if (!this.removed && (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {

            // If the side is null (e.g we'Re in the gui) we return the normal inventory.
            // For world interactions (direction != null) we return the inventory that block slots of not finished recipes.
            //noinspection unchecked
            return (LazyOptional<X>) (side == null ? lazyInventory : hopperInventory);
        }
        return super.getCapability(cap);
    }

    public InventoryHandler getInventory() {
        return inventory;
    }

    public class InventoryHandler extends ItemStackHandler implements IFluidHandler {

        private final List<FluidStack> fluids = new ArrayList<>(8);

        public InventoryHandler() {
            super(8);
            for (int i = 0; i < 8; i++) {
                fluids.add(FluidStack.EMPTY);
            }
            //fluids = NonNullList.from(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000));
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            if (!stack.isEmpty())
                fluids.set(slot, FluidStack.EMPTY);
            super.setStackInSlot(slot, stack);
        }

        public void setStackInSlot(int slot, @Nonnull FluidStack stack) {
            fluids.set(slot, stack);
            if (!stack.isEmpty())
                super.setStackInSlot(slot, ItemStack.EMPTY);
            else
                onContentsChanged(slot); // setStackInSlot calls this as well
        }

        @Override
        public int getSlots() {
            return 8;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!fluids.get(slot).isEmpty()) {
                // Slot is occupied by a fluid.
                return stack;
            } else {
                return super.insertItem(slot, stack, simulate);
            }
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!fluids.get(slot).isEmpty()) {
                // Slot is occupied by a fluid.
                return ItemStack.EMPTY;
            } else {
                return super.extractItem(slot, amount, simulate);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof BlockItem && getRecipe(((BlockItem) stack.getItem()).getBlock().getDefaultState()) != null;
        }

        @Override
        public int getTanks() {
            return 8;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!getStackInSlot(tank).isEmpty()) {
                // There's an item in here
                return FluidStack.EMPTY;
            } else {
                return fluids.get(tank);
            }
        }

        @Override
        public int getTankCapacity(int tank) {
            return 1000;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return !stack.isEmpty() && getRecipe(stack.getFluid().getDefaultState().getBlockState()) != null;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int leftToFill = resource.getAmount();

            // Try to deposit the fluid to slots already filled with it
            for (int i = 0; i < 8; i++) {
                if (leftToFill <= 0)
                    break;
                if (!getStackInSlot(i).isEmpty())
                    continue;
                if (fluids.get(i).getFluid() == resource.getFluid()) {
                    int transfer = Math.min(leftToFill, getTankCapacity(i) - fluids.get(i).getAmount());
                    leftToFill -= transfer;
                    if (action == FluidAction.EXECUTE) {
                        fluids.get(i).setAmount(fluids.get(i).getAmount() + transfer);
                        onContentsChanged(i);
                    }
                }
            }

            // If that did not work we use an empty slot
            for (int i = 0; i < 8; i++) {
                if (leftToFill <= 0)
                    break;
                if (!getStackInSlot(i).isEmpty())
                    continue;
                if (fluids.get(i).isEmpty()) {
                    int transfer = Math.min(leftToFill, getTankCapacity(i));
                    leftToFill -= transfer;
                    if (action == FluidAction.EXECUTE) {
                        fluids.set(i, new FluidStack(resource.getFluid(), transfer));
                        onContentsChanged(i);
                    }
                }
            }

            return resource.getAmount() - leftToFill;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            int leftToDrain = resource.getAmount();

            for (int i = 0; i < 8; i++) {
                if (leftToDrain <= 0)
                    break;
                if (!getStackInSlot(i).isEmpty())
                    continue;
                if (fluids.get(i).getFluid() == resource.getFluid()) {
                    int transfer = Math.min(fluids.get(i).getAmount(), leftToDrain);
                    leftToDrain -= transfer;
                    if (action == FluidAction.EXECUTE) {
                        fluids.get(i).setAmount(fluids.get(i).getAmount() - transfer);
                        if (fluids.get(i).getAmount() <= 0)
                            fluids.set(i, FluidStack.EMPTY);
                        onContentsChanged(i);
                    }
                }
            }

            if (resource.getAmount() - leftToDrain > 0) {
                return new FluidStack(resource.getFluid(), resource.getAmount() - leftToDrain);
            } else {
                return FluidStack.EMPTY;
            }
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            int leftToDrain = maxDrain;
            Fluid drainFluid = null;

            for (int i = 0; i < 8; i++) {
                if (leftToDrain <= 0)
                    break;
                if (!getStackInSlot(i).isEmpty())
                    continue;
                if (drainFluid == null || drainFluid == fluids.get(i).getFluid()) {
                    int transfer = Math.min(fluids.get(i).getAmount(), leftToDrain);
                    leftToDrain -= transfer;
                    if (transfer > 0)
                        drainFluid = fluids.get(i).getFluid();
                    if (action == FluidAction.EXECUTE) {
                        fluids.get(i).setAmount(fluids.get(i).getAmount() - transfer);
                        if (fluids.get(i).getAmount() <= 0)
                            fluids.set(i, FluidStack.EMPTY);
                        onContentsChanged(i);
                    }
                }
            }

            if (drainFluid == null) {
                return FluidStack.EMPTY;
            } else {
                return new FluidStack(drainFluid, maxDrain - leftToDrain);
            }
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = super.serializeNBT();
            ListNBT tag = new ListNBT();
            for (int i = 0; i < 8; i++) {
                CompoundNBT fluidNbt = new CompoundNBT();
                fluids.get(i).writeToNBT(fluidNbt);
                tag.add(i, fluidNbt);
            }
            nbt.put("fluids", tag);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            if (nbt.contains("fluids")) {
                ListNBT tag = nbt.getList("fluids", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < 8; i++) {
                    CompoundNBT fluidNbt = tag.getCompound(i);
                    fluids.set(i, FluidStack.loadFluidStackFromNBT(fluidNbt));
                }
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    }
}
