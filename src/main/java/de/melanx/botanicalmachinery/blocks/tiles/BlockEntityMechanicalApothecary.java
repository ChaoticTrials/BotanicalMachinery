package de.melanx.botanicalmachinery.blocks.tiles;

import com.google.common.collect.Range;
import de.melanx.botanicalmachinery.blocks.base.WorkingTile;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.base.tile.TickableBlock;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import vazkii.botania.api.recipe.ICustomApothecaryColor;
import vazkii.botania.api.recipe.IPetalRecipe;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.handler.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class BlockEntityMechanicalApothecary extends WorkingTile<IPetalRecipe> implements TickableBlock {

    public static final int WORKING_DURATION = 20;
    public static final int FLUID_CAPACITY = 8000;

    private final BaseItemStackHandler inventory;
    
    private final ApothecaryFluidTank fluidInventory = new ApothecaryFluidTank(FLUID_CAPACITY, fluidStack -> Fluids.WATER.isSame(fluidStack.getFluid()));
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.fluidInventory);
    
    private ItemStack currentOutput = ItemStack.EMPTY;

    public BlockEntityMechanicalApothecary(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, ModRecipeTypes.PETAL_TYPE, pos, state, 0, 1, 17);
        this.inventory = BaseItemStackHandler.builder(21)
                .validator(stack -> stack.is(Tags.Items.SEEDS), 0)
                .validator(stack -> this.level != null && RecipeHelper.isItemValidInput(this.level.getRecipeManager(), ModRecipeTypes.PETAL_TYPE, stack), Range.closedOpen(1, 17))
                .output(17, 18, 19, 20)
                .contentsChanged(() -> {
                    this.setChanged();
                    this.setDispatchable();
                    this.needsRecipeUpdate();
                })
                .build();
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            this.runRecipeTick();
            if (this.recipe != null) {
                this.currentOutput = this.recipe.getResultItem().copy();
                this.setChanged();
                this.setDispatchable();
            } else if (!this.currentOutput.isEmpty()) {
                this.currentOutput = ItemStack.EMPTY;
                this.setChanged();
                this.setDispatchable();
            }
        } else if (this.level != null && LibXClientConfig.AdvancedRendering.all && LibXClientConfig.AdvancedRendering.mechanicalApothecary) {
            if (this.fluidInventory.getFluidAmount() > 0) {
                if (this.getProgress() > getMaxProgress() - 5) {
                    for (int i = 0; i < 5; i++) {
                        SparkleParticleData data = SparkleParticleData.sparkle(this.level.random.nextFloat(), this.level.random.nextFloat(), this.level.random.nextFloat(), this.level.random.nextFloat(), 10);
                        this.level.addParticle(data, this.worldPosition.getX() + 0.3 + (this.level.random.nextDouble() * 0.4), this.worldPosition.getY() + 0.6, this.worldPosition.getZ() + 0.3 + (this.level.random.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
                    }
                    this.level.playLocalSound(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, ModSounds.altarCraft, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                } else {
                    for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
                        ItemStack stack = this.inventory.getStackInSlot(slot);
                        if (stack.isEmpty()) {
                            continue;
                        }

                        if (this.level.random.nextFloat() >= 0.97f) {
                            int color = stack.getItem() instanceof ICustomApothecaryColor ? ((ICustomApothecaryColor) stack.getItem()).getParticleColor(stack) : 0x888888;
                            float red = (float) (color >> 16 & 255) / 255f;
                            float green = (float) (color >> 8 & 255) / 255f;
                            float blue = (float) (color & 255) / 255f;
                            if (Math.random() >= 0.75) {
                                this.level.playSound(null, this.worldPosition, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.1F, 10.0F);
                            }
                            SparkleParticleData data = SparkleParticleData.sparkle(this.level.random.nextFloat(), red, green, blue, 10);
                            this.level.addParticle(data, this.worldPosition.getX() + 0.3 + (this.level.random.nextDouble() * 0.4), this.worldPosition.getY() + 0.6, this.worldPosition.getZ() + 0.3 + (this.level.random.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            }
        }
    }
    
    @Nonnull
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Nonnull
    public FluidTank getFluidInventory() {
        return this.fluidInventory;
    }

    @Override
    public boolean actAsMana() {
        return false;
    }

    @Override
    protected int getAndApplyProgressThisTick() {
        return 1;
    }

    @Override
    protected boolean canMatchRecipes() {
        if (this.inventory.getStackInSlot(0).isEmpty()) return false;
        FluidStack fluid = this.getFluidInventory().getFluid();
        return !fluid.isEmpty() && fluid.getFluid() == Fluids.WATER && fluid.getAmount() >= FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    protected void onCrafted(IPetalRecipe recipe) {
        this.inventory.extractItem(0, 1, false);
        FluidStack fluid = this.getFluidInventory().getFluid().copy();
        if (fluid.getFluid() != Fluids.WATER) return;
        int newAmount = Math.max(0, fluid.getAmount() - FluidAttributes.BUCKET_VOLUME);
        fluid.setAmount(newAmount);
        this.fluidInventory.setFluid(fluid);
    }

    @Override
    protected int getMaxProgress(IPetalRecipe recipe) {
        return WORKING_DURATION * LibXServerConfig.WorkingDurationMultiplier.mechanicalApothecary;
    }

    @Override
    public int getMaxManaPerTick() {
        return 1;
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.fluidInventory.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound(TileTags.FLUID)));
        this.currentOutput = ItemStack.of(nbt.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag tankTag = new CompoundTag();
        this.getFluidInventory().getFluid().writeToNBT(tankTag);
        nbt.put(TileTags.FLUID, tankTag);
        nbt.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        if (this.level != null && !this.level.isClientSide) return;
        this.fluidInventory.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound(TileTags.FLUID)));
        this.currentOutput = ItemStack.of(nbt.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        final CompoundTag tankTag = new CompoundTag();
        this.getFluidInventory().getFluid().writeToNBT(tankTag);
        nbt.put(TileTags.FLUID, tankTag);
        nbt.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return nbt;
    }

    private class ApothecaryFluidTank extends FluidTank {

        public ApothecaryFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            BlockEntityMechanicalApothecary.this.setChanged();
            BlockEntityMechanicalApothecary.this.setDispatchable();
            BlockEntityMechanicalApothecary.this.needsRecipeUpdate();
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
