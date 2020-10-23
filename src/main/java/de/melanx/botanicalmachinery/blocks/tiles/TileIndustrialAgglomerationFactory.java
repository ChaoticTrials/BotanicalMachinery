package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import vazkii.botania.api.recipe.ITerraPlateRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileIndustrialAgglomerationFactory extends BotanicalTile implements IWorkingTile {

    public static final int MAX_MANA_PER_TICK = 5000;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4, slot -> this.markDispatchable(), this::isValidStack);

    private int progress;
    private int maxProgress = -1;
    private ITerraPlateRecipe recipe;

    public TileIndustrialAgglomerationFactory(TileEntityType<?> type) {
        super(type, ServerConfig.capacityAgglomerationFactory.get());
        this.inventory.setInputSlots(0, 1, 2);
        this.inventory.setOutputSlots(3);
        this.inventory.setSlotValidator(this::isValidStack);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (Arrays.stream(this.inventory.getInputSlots()).anyMatch(x -> x == slot) && this.world != null)
            return RecipeHelper.isItemValidInput(this.world.getRecipeManager(), ModRecipeTypes.TERRA_PLATE_TYPE, stack);
        return true;
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.remove(3);

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof ITerraPlateRecipe) {
                    if (RecipeHelper.matches(recipe, stacks, false)) {
                        this.recipe = (ITerraPlateRecipe) recipe;
                        this.markDispatchable();
                        return;
                    }
                }
            }
        }
        this.recipe = null;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            this.updateRecipe();
            if (this.recipe != null) {
                ItemStack output = this.recipe.getRecipeOutput().copy();
                ItemStack currentOutput = this.inventory.getStackInSlot(3);
                if (this.getInventory().getStackInSlot(3).isEmpty() ||
                        (output.getItem() == currentOutput.getItem() && currentOutput.getMaxStackSize() > currentOutput.getCount())) {
                    this.maxProgress = this.recipe.getMana();
                    int manaTransfer = Math.min(this.getCurrentMana(), Math.min(this.getMaxManaPerTick(), this.getMaxProgress() - this.progress));
                    this.progress += manaTransfer;
                    this.receiveMana(-manaTransfer);
                    if (this.progress >= this.getMaxProgress()) {
                        for (Ingredient ingredient : this.recipe.getIngredients()) {
                            for (int slot : this.inventory.getInputSlots()) {
                                ItemStack stack = this.inventory.getStackInSlot(slot);
                                if (ingredient.test(stack)) {
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                        this.inventory.getUnrestricted().insertItem(3, output, false);
                        this.progress = 0;
                        this.updateRecipe();
                    }
                    this.markDirty();
                }
            } else if (this.progress > 0) {
                this.progress = 0;
                this.maxProgress = -1;
                this.markDirty();
                this.markDispatchable();
            }
        } else if (this.world != null && ClientConfig.everything.get() && ClientConfig.agglomerationFactory.get()) {
            if (this.progress > 0) {
                double time = this.progress / (double) this.getMaxProgress();
                if (time < 0.8) {
                    time = time * 1.25;
                    double y = this.pos.getY() + 6 / 16d + ((5 / 16d) * time);
                    double x1 = this.pos.getX() + 0.2 + (0.3 * time);
                    double x2 = this.pos.getX() + 0.8 - (0.3 * time);
                    double z1 = this.pos.getZ() + 0.2 + (0.3 * time);
                    double z2 = this.pos.getZ() + 0.8 - (0.3 * time);
                    WispParticleData data = WispParticleData.wisp(0.1f, 0, (float) time, (float) (1 - time), 1);
                    this.world.addParticle(data, x1, y, z1, 0, 0, 0);
                    this.world.addParticle(data, x1, y, z2, 0, 0, 0);
                    this.world.addParticle(data, x2, y, z1, 0, 0, 0);
                    this.world.addParticle(data, x2, y, z2, 0, 0, 0);
                }
            }
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK / ServerConfig.multiplierAgglomerationFactory.get();
    }

    @Override
    public int getComparatorOutput() {
        return this.getProgress() > 0 ? 15 : 0;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT cmp) {
        super.read(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.maxProgress = cmp.getInt(TileTags.MAX_PROGRESS);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT cmp) {
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        return super.write(cmp);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT cmp) {
        if (this.world != null && !this.world.isRemote) return;
        super.handleUpdateTag(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.maxProgress = cmp.getInt(TileTags.MAX_PROGRESS);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        if (this.world != null && this.world.isRemote) return super.getUpdateTag();
        CompoundNBT cmp = super.getUpdateTag();
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        return cmp;
    }
}
