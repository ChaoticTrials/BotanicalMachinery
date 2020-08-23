package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.Explosion;
import vazkii.botania.api.recipe.IElvenTradeRecipe;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.IntStream;

public class TileAlfheimMarket extends TileBase {

    public static final int WORKING_DURATION = 20;
    private static final int RECIPE_COST = 500;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(5, slot -> this.update = true, this::isValidStack);
    private IElvenTradeRecipe recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;

    private static final String TAG_PROGRESS = "progress";

    public TileAlfheimMarket() {
        super(Registration.TILE_ALFHEIM_MARKET.get(), 500_000);
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
        return Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot) || RecipeHelper.isItemValid(this.world, ModRecipeTypes.ELVEN_TRADE_TYPE, stack);
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            stacks.remove(4);
            Map<Item, Integer> items = new HashMap<>();
            stacks.removeIf(stack -> stack.getItem() == Blocks.AIR.asItem());
            stacks.forEach(stack -> {
                Item item = stack.getItem();
                if (!items.containsKey(item)) {
                    items.put(item, stack.getCount());
                } else {
                    int prevCount = items.get(item);
                    items.replace(item, prevCount, prevCount + stack.getCount());
                }
            });

            for (IRecipe<?> recipe : this.world.getRecipeManager().getRecipes()) {
                if (recipe instanceof IElvenTradeRecipe) {
                    if (RecipeHelper.checkIngredients(stacks, items, recipe)) {
                        this.recipe = (IElvenTradeRecipe) recipe;
                        return;
                    }
                }
            }
        }
        this.recipe = null;
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);
        cmp.putInt(TAG_PROGRESS, this.progress);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInt(TAG_PROGRESS);
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
                        if (this.getCurrentMana() >= RECIPE_COST || this.progress > 0 && this.progress <= WORKING_DURATION) {
                            ++this.progress;
                            this.receiveMana(-(RECIPE_COST / WORKING_DURATION));
                            if (this.progress >= WORKING_DURATION) {
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
            }
            if (this.update) {
                this.updateRecipe();
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
}
