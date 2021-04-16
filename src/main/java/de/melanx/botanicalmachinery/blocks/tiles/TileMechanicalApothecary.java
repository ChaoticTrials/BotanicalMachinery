package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.helper.RecipeHelper2;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.inventory.ItemStackHandlerWrapper;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.recipe.ICustomApothecaryColor;
import vazkii.botania.api.recipe.IPetalRecipe;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TileMechanicalApothecary extends TileEntityBase implements ITickableTileEntity {

    public static final int WORKING_DURATION = 20;
    public static final int FLUID_CAPACITY = 8000;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(21, slot -> {
        this.update = true;
        this.markDispatchable();
    }, this::isValidStack);

    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.createLazy(this::getInventory);

    private final ApothecaryFluidTank fluidInventory = new ApothecaryFluidTank(FLUID_CAPACITY, fluidStack -> Fluids.WATER.isEquivalentTo(fluidStack.getFluid()));
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.fluidInventory);
    private IPetalRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;
    private ItemStack currentOutput = ItemStack.EMPTY;

    public TileMechanicalApothecary(TileEntityType<?> type) {
        super(type);
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
        if (this.world == null) return false;
        if (slot == 0) return Tags.Items.SEEDS.contains(stack.getItem());
        else if (Arrays.stream(this.inventory.getInputSlots()).anyMatch(x -> x == slot))
            return RecipeHelper.isItemValidInput(this.world.getRecipeManager(), ModRecipeTypes.PETAL_TYPE, stack);
        return true;
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            RecipeHelper2.removeFromList(stacks, IntStream.range(17, stacks.size() - 1).toArray(), new int[]{0});

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IPetalRecipe) {
                    if (RecipeHelper.matches(recipe, stacks, false) && !this.inventory.getStackInSlot(0).isEmpty() && this.fluidInventory.getFluidAmount() >= 1000) {
                        this.recipe = (IPetalRecipe) recipe;
                        this.currentOutput = this.recipe.getRecipeOutput().copy();
                        this.markDispatchable();
                        return;
                    }
                }
            }
        }
        this.currentOutput = ItemStack.EMPTY;
        this.recipe = null;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (this.recipe != null) {
                if (this.progress <= getRecipeDuration()) {
                    ++this.progress;
                    if (this.progress >= getRecipeDuration()) {
                        ItemStack output = this.recipe.getRecipeOutput().copy();
                        for (Ingredient ingredient : this.recipe.getIngredients()) {
                            for (int slot : this.inventory.getInputSlots()) {
                                ItemStack stack = this.inventory.getStackInSlot(slot);
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
        } else if (this.world != null && LibXClientConfig.AdvancedRendering.all && LibXClientConfig.AdvancedRendering.mechanicalApothecary) {
            if (this.fluidInventory.getFluidAmount() > 0) {
                if (this.progress > getRecipeDuration() - 5) {
                    for (int i = 0; i < 5; i++) {
                        SparkleParticleData data = SparkleParticleData.sparkle(this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), 10);
                        this.world.addParticle(data, this.pos.getX() + 0.3 + (this.world.rand.nextDouble() * 0.4), this.pos.getY() + 0.6, this.pos.getZ() + 0.3 + (this.world.rand.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
                    }
                    this.world.playSound(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, ModSounds.altarCraft, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                } else {
                    for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
                        ItemStack stack = this.inventory.getStackInSlot(slot);
                        if (stack.isEmpty()) {
                            continue;
                        }

                        if (this.world.rand.nextFloat() >= 0.97f) {
                            int color = stack.getItem() instanceof ICustomApothecaryColor ? ((ICustomApothecaryColor) stack.getItem()).getParticleColor(stack) : 0x888888;
                            float red = (float) (color >> 16 & 255) / 255f;
                            float green = (float) (color >> 8 & 255) / 255f;
                            float blue = (float) (color & 255) / 255f;
                            if (Math.random() >= 0.75) {
                                this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1F, 10.0F);
                            }
                            SparkleParticleData data = SparkleParticleData.sparkle(this.world.rand.nextFloat(), red, green, blue, 10);
                            this.world.addParticle(data, this.pos.getX() + 0.3 + (this.world.rand.nextDouble() * 0.4), this.pos.getY() + 0.6, this.pos.getZ() + 0.3 + (this.world.rand.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
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

    public int getProgress() {
        return this.progress;
    }

    public static int getRecipeDuration() {
        return WORKING_DURATION * LibXServerConfig.WorkingDurationMultiplier.mechanicalApothecary;
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
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

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT cmp) {
        super.read(state, cmp);
        this.getInventory().deserializeNBT(cmp.getCompound(TileTags.INVENTORY));
        this.fluidInventory.setFluid(FluidStack.loadFluidStackFromNBT(cmp.getCompound(TileTags.FLUID)));
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.currentOutput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT cmp) {
        cmp.put(TileTags.INVENTORY, this.getInventory().serializeNBT());
        CompoundNBT tankTag = new CompoundNBT();
        this.getFluidInventory().getFluid().writeToNBT(tankTag);
        cmp.put(TileTags.FLUID, tankTag);
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return cmp;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT cmp) {
        if (this.world != null && !this.world.isRemote) return;
        this.getInventory().deserializeNBT(cmp.getCompound(TileTags.INVENTORY));
        this.fluidInventory.setFluid(FluidStack.loadFluidStackFromNBT(cmp.getCompound(TileTags.FLUID)));
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.currentOutput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        if (this.world != null && this.world.isRemote) return super.getUpdateTag();
        CompoundNBT cmp = super.getUpdateTag();
        cmp.put(TileTags.INVENTORY, this.getInventory().serializeNBT());
        final CompoundNBT tankTag = new CompoundNBT();
        this.getFluidInventory().getFluid().writeToNBT(tankTag);
        cmp.put(TileTags.FLUID, tankTag);
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return cmp;
    }

    private class ApothecaryFluidTank extends FluidTank {

        public ApothecaryFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            TileMechanicalApothecary.this.markDispatchable();
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
