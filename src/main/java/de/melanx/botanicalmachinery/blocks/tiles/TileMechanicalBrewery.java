package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.helper.RecipeHelper2;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.api.recipe.IBrewRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TileMechanicalBrewery extends BotanicalTile implements IWorkingTile {

    public static final int MAX_MANA_PER_TICK = 50;

    public static final List<Item> BREW_CONTAINER = Arrays.asList(ModItems.vial.asItem(), ModItems.flask.asItem(), ModItems.incenseStick.asItem(), ModItems.bloodPendant.asItem());

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(8, slot -> {
        this.update = true;
        this.markDispatchable();
    }, this::isValidStack);

    private IBrewRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private int maxProgress = -1;
    private boolean update;
    private ItemStack currentOutput = ItemStack.EMPTY;

    public TileMechanicalBrewery(TileEntityType<?> type) {
        super(type, ServerConfig.capacityBrewery.get());
        this.inventory.setInputSlots(IntStream.range(0, 7).toArray());
        this.inventory.setOutputSlots(7);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (world == null) return false;
        if (slot == 0)
            return stack.getTag() != null ? !stack.getTag().contains("brewKey") : BREW_CONTAINER.contains(stack.getItem());
        return (Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot)) || RecipeHelper.isItemValidInput(this.world.getRecipeManager(), ModRecipeTypes.BREW_TYPE, stack);
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            if (this.inventory.getStackInSlot(0).isEmpty()) {
                this.recipe = null;
                return;
            }
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            RecipeHelper2.removeFromList(stacks, new int[]{0, 7});

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IBrewRecipe) {
                    if (RecipeHelper.matches(recipe, stacks, false)) {
                        this.recipe = (IBrewRecipe) recipe;
                        if (this.inventory.getStackInSlot(0).isEmpty() || !(this.inventory.getStackInSlot(0).getItem() instanceof IBrewContainer)) {
                            this.currentOutput = ItemStack.EMPTY;
                        } else {
                            this.currentOutput = ((IBrewContainer) this.inventory.getStackInSlot(0).getItem()).getItemForBrew(this.recipe.getBrew(), this.inventory.getStackInSlot(0).copy());
                        }
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
        if (!this.initDone) {
            this.update = true;
            this.initDone = true;
        }
        if (this.world != null && !this.world.isRemote) {
            this.updateRecipe(); // todo remove
            boolean done = false;
            if (this.recipe != null) {
                ItemStack output = this.recipe.getOutput(this.inventory.getStackInSlot(0)).copy();
                ItemStack currentOutput = this.inventory.getStackInSlot(7);
                if (!output.isEmpty() && (currentOutput.isEmpty() || (ItemStack.areItemStacksEqual(output, currentOutput) && currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize()))) {
                    this.maxProgress = this.getManaCost();
                    int manaTransfer = Math.min(this.getCurrentMana(), Math.min(MAX_MANA_PER_TICK, this.getMaxProgress() - this.progress));
                    this.progress += manaTransfer;
                    this.receiveMana(-manaTransfer);
                    if (this.progress >= this.getMaxProgress()) {
                        if (currentOutput.isEmpty()) {
                            this.inventory.setStackInSlot(7, output);
                        } else {
                            currentOutput.setCount(currentOutput.getCount() + output.getCount());
                        }
                        this.inventory.getStackInSlot(0).shrink(1);
                        for (Ingredient ingredient : this.recipe.getIngredients()) {
                            for (ItemStack stack : this.inventory.getStacks()) {
                                if (ingredient.test(stack)) {
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                        this.update = true;
                        done = true;
                    }
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.maxProgress = -1;
                this.markDirty();
                this.markDispatchable();
            }
        } else if (this.world != null) {
            if (this.progress > 0 && ClientConfig.everything.get() && ClientConfig.brewery.get()) {
                if (this.currentOutput.getItem() instanceof IBrewItem && this.world.rand.nextFloat() < 0.5f) {
                    int segments = 3;
                    for (int i = 1; i <= 6; i++) {
                        if (!this.inventory.getStackInSlot(i).isEmpty()) {
                            segments += 1;
                        }
                    }
                    if (this.progress < (segments - 1) * (this.maxProgress / (double) segments) && this.progress > (segments - 2) * (this.maxProgress / (double) segments)) {
                        int targetColor = ((IBrewItem) this.currentOutput.getItem()).getBrew(this.currentOutput).getColor(this.currentOutput);
                        float red = (targetColor >> 16 & 255) / 255f;
                        float green = (targetColor >> 8 & 255) / 255f;
                        float blue = (targetColor & 255) / 255f;
                        WispParticleData data = WispParticleData.wisp(0.125f, red, green, blue, 0.5f);
                        double xPos = this.pos.getX() + 0.25 + (this.world.rand.nextDouble() / 2);
                        double zPos = this.pos.getZ() + 0.25 + (this.world.rand.nextDouble() / 2);
                        this.world.addParticle(data, xPos, this.pos.getY() + 0.35, zPos, 0, 0.01 + (this.world.rand.nextDouble() / 18), 0);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasValidRecipe() {
        return true;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK / ServerConfig.multiplierBrewery.get();
    }

    public int getManaCost() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (this.recipe == null || stack.isEmpty() || !(stack.getItem() instanceof IBrewContainer)) {
            return 0;
        }
        IBrewContainer container = (IBrewContainer) stack.getItem();
        return container.getManaCost(this.recipe.getBrew(), stack);
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }

    @Override
    public int getComparatorOutput() {
        return 0;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT cmp) {
        super.read(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.maxProgress = cmp.getInt(TileTags.MAX_PROGRESS);
        this.currentOutput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT cmp) {
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        cmp.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return super.write(cmp);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT cmp) {
        if (world != null && !world.isRemote) return;
        super.handleUpdateTag(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.maxProgress = cmp.getInt(TileTags.MAX_PROGRESS);
        this.currentOutput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        if (world != null && world.isRemote) return super.getUpdateTag();
        CompoundNBT cmp = super.getUpdateTag();
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.putInt(TileTags.MAX_PROGRESS, this.maxProgress);
        cmp.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return cmp;
    }
}
