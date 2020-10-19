package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileMechanicalManaPool extends TileBase {
    public static final List<Item> CATALYSTS = Arrays.asList(ModBlocks.alchemyCatalyst.asItem(), ModBlocks.conjurationCatalyst.asItem(), ModBlocks.manaVoid.asItem());
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(3, this::onSlotChanged, this::isValidStack);
    public boolean validRecipe = true;
    private int cooldown = ServerConfig.multiplierManaPool.get();

    public TileMechanicalManaPool() {
        super(Registration.TILE_MECHANICAL_MANA_POOL.get(), ServerConfig.capacityManaPool.get());
        this.inventory.addSlotLimit(0, 1);
        this.inventory.setOutputSlots(2);
    }

    public IManaInfusionRecipe getMatchingRecipe(@Nonnull ItemStack stack, @Nonnull ItemStack cat) {
        List<IManaInfusionRecipe> matchingNonCatRecipes = new ArrayList<>();
        List<IManaInfusionRecipe> matchingCatRecipes = new ArrayList<>();

        //noinspection ConstantConditions
        for (IManaInfusionRecipe recipe : TilePool.manaInfusionRecipes(this.world)) {
            if (recipe.matches(stack)) {
                if (recipe.getCatalyst() == null) {
                    matchingNonCatRecipes.add(recipe);
                } else if (recipe.getCatalyst().getBlock().asItem() == cat.getItem()) {
                    matchingCatRecipes.add(recipe);
                }
            }
        }

        // Recipes with matching catalyst take priority above recipes with no catalyst specified
        return !matchingCatRecipes.isEmpty() ? matchingCatRecipes.get(0) : !matchingNonCatRecipes.isEmpty() ? matchingNonCatRecipes.get(0) : null;
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    private void onSlotChanged(int slot) {
        if (slot == 1) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            IManaInfusionRecipe recipe = this.getMatchingRecipe(stack, cat);
            if (recipe != null) {
                this.validRecipe = recipe.getManaToConsume() <= this.getCurrentMana();
            } else {
                this.validRecipe = stack.isEmpty();
            }
        }
        this.sendPacket = true;
        this.markDirty();
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot == 0) return CATALYSTS.contains(stack.getItem());
        if (slot == 1) return RecipeHelper.isItemValid(this.world, ModRecipeTypes.MANA_INFUSION_TYPE, stack);
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!ManaNetworkHandler.instance.isPoolIn(this) && !this.isRemoved()) {
            ManaNetworkEvent.addCollector(this);
        }

        if (this.world != null) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            IManaInfusionRecipe recipe = this.getMatchingRecipe(stack, cat);
            if (!this.world.isRemote) {
                if (recipe != null) {
                    if (this.getCooldown() > 1) {
                        this.cooldown--;
                    } else {
                        int mana = recipe.getManaToConsume();
                        if (this.getCurrentMana() >= mana && (this.getInventory().getStackInSlot(2).isEmpty() ||
                                (recipe.getRecipeOutput().getItem() == this.getInventory().getStackInSlot(2).getItem() &&
                                        this.getInventory().getStackInSlot(2).getMaxStackSize() > this.getInventory().getStackInSlot(2).getCount()))) {
                            this.receiveMana(-mana);
                            stack.shrink(1);

                            ItemStack output = recipe.getRecipeOutput().copy();
                            this.inventory.getUnrestricted().insertItem(2, output, false);
                            this.markDirty();
                            this.cooldown = ServerConfig.multiplierManaPool.get();
                        }
                    }
                }
            } else if (ClientConfig.everything.get() && ClientConfig.agglomerationFactory.get()) {
                double particleChance = (this.getCurrentMana() / (double) this.getManaCap()) * 0.1D;
                if (Math.random() < particleChance) {
                    float red = 0.0F;
                    float green = 0.7764706F;
                    float blue = 1.0F;
                    WispParticleData data = WispParticleData.wisp((float) Math.random() / 3.0F, red, green, blue, 2.0F);
                    this.world.addParticle(data, this.pos.getX() + 0.3D + (this.world.rand.nextDouble() * 0.4), this.pos.getY() + 0.5D + (this.world.rand.nextDouble() * 0.25D), this.pos.getZ() + 0.3D + (this.world.rand.nextDouble() * 0.4), 0, this.world.rand.nextFloat() / 25, 0);
                }
            }
        }
    }

    @Override
    public boolean hasValidRecipe() {
        return this.validRecipe;
    }

    @Override
    public void receiveMana(int i) {
        if (this.inventory.getStackInSlot(0).getItem() == ModBlocks.manaVoid.asItem())
            this.mana = Math.min(this.getCurrentMana() + i, this.getManaCap());
        else super.receiveMana(i);
    }

    @Override
    public boolean isFull() {
        return this.inventory.getStackInSlot(0).getItem() != ModBlocks.manaVoid.asItem() && super.isFull();
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
