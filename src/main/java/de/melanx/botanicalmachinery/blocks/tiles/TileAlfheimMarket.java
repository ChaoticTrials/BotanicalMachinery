package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.Explosion;
import vazkii.botania.api.recipe.IElvenTradeRecipe;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TileAlfheimMarket extends TileBase implements IWorkingTile {

    private static final int RECIPE_COST = ServerConfig.alfheimMarketRecipeCost.get();
    public static final int MAX_MANA_PER_TICK = 25;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(5, slot -> {
        this.update = true;
        this.sendPacket = true;
    }, this::isValidStack);
    private IElvenTradeRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;
    private ItemStack currentInput = ItemStack.EMPTY;
    private ItemStack currentOutput = ItemStack.EMPTY;

    public TileAlfheimMarket(TileEntityType<?> type) {
        super(type, ServerConfig.capacityAlfheimMarket.get());
        this.inventory.setInputSlots(IntStream.range(0, 4).toArray());
        this.inventory.setOutputSlots(4);
        this.update = true;
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (world == null) return false;
        return Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot) || RecipeHelper.isItemValidInput(this.world.getRecipeManager(), ModRecipeTypes.ELVEN_TRADE_TYPE, stack);
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.remove(4);

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IElvenTradeRecipe) {
                    if (RecipeHelper.matches(recipe, stacks, false)) {
                        this.recipe = (IElvenTradeRecipe) recipe;
                        this.currentInput = getInputStack(this.recipe).copy();
                        this.currentOutput = this.recipe.getOutputs().get(0).copy();
                        this.sendPacket = true;
                        return;
                    }
                }
            }
        }
        this.currentInput = ItemStack.EMPTY;
        this.currentOutput = ItemStack.EMPTY;
        this.recipe = null;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT cmp) {
        super.read(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.currentInput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_INPUT));
        this.currentOutput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT cmp) {
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.put(TileTags.CURRENT_INPUT, this.currentInput.serializeNBT());
        cmp.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return super.write(cmp);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT cmp) {
        if (world != null && !world.isRemote) return;
        super.handleUpdateTag(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
        this.currentInput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_INPUT));
        this.currentOutput = ItemStack.read(cmp.getCompound(TileTags.CURRENT_OUTPUT));
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        if (world != null && world.isRemote) return super.getUpdateTag();
        CompoundNBT cmp = super.getUpdateTag();
        cmp.putInt(TileTags.PROGRESS, this.progress);
        cmp.put(TileTags.CURRENT_INPUT, this.currentInput.serializeNBT());
        cmp.put(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
        return cmp;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world != null && !this.world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (this.recipe != null) {
                List<ItemStack> outputs = new ArrayList<>(this.recipe.getOutputs());
                if (outputs.size() == 1) {
                    if (this.inventory.getUnrestricted().insertItem(4, outputs.get(0), true).isEmpty()) {
                        int manaTransfer = Math.min(this.mana, Math.min(this.getMaxManaPerTick(), this.getMaxProgress() - this.progress));
                        this.progress += manaTransfer;
                        this.receiveMana(-manaTransfer);
                        if (this.progress >= RECIPE_COST) {
                            this.inventory.getUnrestricted().insertItem(4, outputs.get(0).copy(), false);
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
                            this.markDirty();
                            this.markDispatchable();
                        }
                    }
                }
            }
            if (this.update) {
                this.updateRecipe();
                this.markDirty();
                this.update = false;
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            }
            if (this.mana > 0) {
                for (int i : this.inventory.getInputSlots()) {
                    if (this.inventory.getStackInSlot(i).getItem() == Items.BREAD) {
                        this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
                        this.world.createExplosion(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3F, Explosion.Mode.BREAK);
                        break;
                    }
                }
            }
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return RECIPE_COST;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK * ServerConfig.multiplierAlfheimMarket.get();
    }

    private static ItemStack getInputStack(IElvenTradeRecipe recipe) {
        if (recipe.getIngredients().isEmpty())
            return ItemStack.EMPTY;
        ItemStack[] stacks = recipe.getIngredients().get(0).getMatchingStacks();
        if (stacks.length == 0)
            return ItemStack.EMPTY;
        return stacks[0];
    }

    public ItemStack getCurrentInput() {
        return this.currentInput;
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }
}
