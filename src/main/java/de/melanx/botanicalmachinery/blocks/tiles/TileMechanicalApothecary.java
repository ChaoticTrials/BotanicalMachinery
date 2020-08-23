package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.Tags;
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
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TileMechanicalApothecary extends TileMod implements ITickableTileEntity {

    public static final int WORKING_DURATION = 20;
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
        this.inventory.setInputSlots(IntStream.range(1, 17).toArray());
        this.inventory.setOutputSlots(IntStream.range(17, 21).toArray());
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
        if (slot == 0) return Tags.Items.SEEDS.contains(stack.getItem());
        else if (Arrays.stream(this.inventory.getInputSlots()).anyMatch(x -> x == slot))
            return RecipeHelper.isItemValid(this.world, ModRecipeTypes.PETAL_TYPE, stack);
        return true;
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            RecipeHelper.removeFromList(stacks, IntStream.range(17, stacks.size() - 1).toArray(), new int[]{0});
            Map<Item, Integer> items = RecipeHelper.getInvItems(stacks);

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IPetalRecipe) {
                    if (RecipeHelper.checkIngredients(stacks, items, recipe) && !this.inventory.getStackInSlot(0).isEmpty() && this.fluidInventory.getFluidAmount() >= 1000) {
                        this.recipe = (IPetalRecipe) recipe;
                        return;
                    }
                }
            }
        }
        this.recipe = null;
    }

    @Override
    public void tick() {
        if (this.sendPacket) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            this.sendPacket = false;
        }
        if (this.world != null && !this.world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (this.recipe != null) {
                if (this.progress <= WORKING_DURATION) {
                    ++this.progress;
                    if (this.progress >= WORKING_DURATION) {
                        ItemStack output = this.recipe.getRecipeOutput().copy();
                        for (Ingredient ingredient : this.recipe.getIngredients()) {
                            for (ItemStack stack : this.inventory.getStacks()) {
                                if (ingredient.test(stack)) {
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                        FluidStack fluid = this.fluidInventory.getFluid();
                        fluid.shrink(1000);
                        this.fluidInventory.setFluid(fluid);
                        this.inventory.getStackInSlot(0).shrink(1);
                        this.putIntoOutput(output);
                        this.update = true;
                        done = true;
                    }
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
        }
    }

    private void putIntoOutput(ItemStack stack) {
        for (int i : this.inventory.getOutputSlots()) {
            if (stack.isEmpty()) break;
            ItemStack slotStack = this.inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                this.inventory.getUnrestricted().insertItem(i, stack.copy(), false);
                break;
            } else if ((slotStack.getItem() == stack.getItem() && slotStack.getCount() < slotStack.getMaxStackSize())) {
                ItemStack left = this.inventory.getUnrestricted().insertItem(i, stack, false);
                if (left != ItemStack.EMPTY) stack = left;
                else break;
            }
        }
    }

    private void markDispatchable() {
        this.sendPacket = true;
    }

    public int getProgress() {
        return this.progress;
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
