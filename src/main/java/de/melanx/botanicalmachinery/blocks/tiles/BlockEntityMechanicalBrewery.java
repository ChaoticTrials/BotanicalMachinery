package de.melanx.botanicalmachinery.blocks.tiles;

import com.google.common.collect.Range;
import de.melanx.botanicalmachinery.blocks.base.WorkingTile;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.api.recipe.IBrewRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEntityMechanicalBrewery extends WorkingTile<IBrewRecipe> {

    public static final int MAX_MANA_PER_TICK = 50;
    public static final List<Item> BREW_CONTAINER = List.of(ModItems.vial.asItem(), ModItems.flask.asItem(), ModItems.incenseStick.asItem(), ModItems.bloodPendant.asItem());

    private final BaseItemStackHandler inventory;
    
    private ItemStack currentOutput = ItemStack.EMPTY;

    public BlockEntityMechanicalBrewery(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, ModRecipeTypes.BREW_TYPE, pos, state, LibXServerConfig.MaxManaCapacity.mechanicalBrewery, 1, 7);
        this.inventory = BaseItemStackHandler.builder(8)
                .validator(stack -> BREW_CONTAINER.contains(stack.getItem()) && (stack.getTag() == null || !stack.getTag().contains("brewKey")), 0)
                .validator(stack -> this.level != null && RecipeHelper.isItemValidInput(this.level.getRecipeManager(), ModRecipeTypes.BREW_TYPE, stack), Range.closedOpen(1, 7))
                .output(7)
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
                this.currentOutput = recipe.getOutput(this.inventory.getStackInSlot(0));
                this.setChanged();
                this.setDispatchable();
            } else if (!this.currentOutput.isEmpty()) {
                this.currentOutput = ItemStack.EMPTY;
                this.setChanged();
                this.setDispatchable();
            }
        } else if (this.level != null) {
            if (this.getProgress() > 0 && LibXClientConfig.AdvancedRendering.all && LibXClientConfig.AdvancedRendering.mechanicalBrewery) {
                if (this.currentOutput.getItem() instanceof IBrewItem && this.level.random.nextFloat() < 0.5f) {
                    int segments = 3;
                    for (int i = 1; i <= 6; i++) {
                        if (!this.inventory.getStackInSlot(i).isEmpty()) {
                            segments += 1;
                        }
                    }
                    if (this.getProgress() < (segments - 1) * (this.getMaxProgress() / (double) segments) && this.getProgress() > (segments - 2) * (this.getMaxProgress() / (double) segments)) {
                        int targetColor = ((IBrewItem) this.currentOutput.getItem()).getBrew(this.currentOutput).getColor(this.currentOutput);
                        float red = (targetColor >> 16 & 255) / 255f;
                        float green = (targetColor >> 8 & 255) / 255f;
                        float blue = (targetColor & 255) / 255f;
                        WispParticleData data = WispParticleData.wisp(0.125f, red, green, blue, 0.5f);
                        double xPos = this.worldPosition.getX() + 0.25 + (this.level.random.nextDouble() / 2);
                        double zPos = this.worldPosition.getZ() + 0.25 + (this.level.random.nextDouble() / 2);
                        this.level.addParticle(data, xPos, this.worldPosition.getY() + 0.35, zPos, 0, 0.01 + (this.level.random.nextDouble() / 18), 0);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    protected boolean canMatchRecipes() {
        return !this.inventory.getStackInSlot(0).isEmpty() && BREW_CONTAINER.contains(this.inventory.getStackInSlot(0).getItem());
    }

    @Override
    protected List<ItemStack> resultItems(IBrewRecipe recipe, List<ItemStack> stacks) {
        return List.of(recipe.getOutput(this.inventory.getStackInSlot(0)));
    }

    @Override
    protected void onCrafted(IBrewRecipe recipe) {
        this.inventory.getUnrestricted().extractItem(0, 1, false);
    }

    @Override
    protected int getMaxProgress(IBrewRecipe recipe) {
        return recipe.getManaUsage();
    }

    @Override
    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK * LibXServerConfig.WorkingDurationMultiplier.mechanicalBrewery;
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.currentOutput = ItemStack.of(nbt.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag tankTag = new CompoundTag();
        nbt.put(TileTags.FLUID, tankTag);
        nbt.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        if (this.level != null && !this.level.isClientSide) return;
        this.currentOutput = ItemStack.of(nbt.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        final CompoundTag tankTag = new CompoundTag();
        nbt.put(TileTags.FLUID, tankTag);
        nbt.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return nbt;
    }
}
