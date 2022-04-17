package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.RecipeTile;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class BlockEntityMechanicalManaPool extends RecipeTile<IManaInfusionRecipe> {

    public static final List<Item> CATALYSTS = Arrays.asList(ModBlocks.alchemyCatalyst.asItem(), ModBlocks.conjurationCatalyst.asItem(), ModBlocks.manaVoid.asItem());

    private final BaseItemStackHandler inventory;

    private int cooldown = 0;
    private boolean checkWithCatalyst = false;

    public BlockEntityMechanicalManaPool(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, ModRecipeTypes.MANA_INFUSION_TYPE, pos, state, LibXServerConfig.MaxManaCapacity.mechanicalManaPool, 1, 2);
        this.inventory = BaseItemStackHandler.builder(3)
                .validator(stack -> CATALYSTS.contains(stack.getItem()), 0)
                .validator(stack -> this.level != null && RecipeHelper.isItemValidInput(this.level.getRecipeManager(), ModRecipeTypes.MANA_INFUSION_TYPE, stack))
                .slotLimit(1, 0)
                .output(2)
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
            this.updateRecipeIfNeeded();
            if (this.cooldown > 0) {
                this.cooldown -= 1;
                this.setChanged();
                this.setDispatchable();
            }
            if (this.cooldown <= 0 && this.recipe != null) {
                this.craftRecipe();
            }
        } else if (this.level != null && LibXClientConfig.AdvancedRendering.all && LibXClientConfig.AdvancedRendering.industrialAgglomerationFactory) {
            double particleChance = (this.getCurrentMana() / (double) this.getManaCap()) * 0.1D;
            if (Math.random() < particleChance) {
                float red = 0.0F;
                float green = 0.7764706F;
                float blue = 1.0F;
                WispParticleData data = WispParticleData.wisp((float) Math.random() / 3.0F, red, green, blue, 2.0F);
                this.level.addParticle(data, this.worldPosition.getX() + 0.3D + (this.level.random.nextDouble() * 0.4), this.worldPosition.getY() + 0.5D + (this.level.random.nextDouble() * 0.25D), this.worldPosition.getZ() + 0.3D + (this.level.random.nextDouble() * 0.4), 0, this.level.random.nextFloat() / 25, 0);
            }
        }
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    protected void updateRecipe(BiConsumer<ItemStack, Integer> usedStacks) {
        this.recipe = null;
        this.checkWithCatalyst = true;
        super.updateRecipe(usedStacks);
        this.checkWithCatalyst = false;
        if (this.recipe == null) {
            super.updateRecipe(usedStacks);
        }
    }

    @Override
    protected boolean matchRecipe(IManaInfusionRecipe recipe, List<ItemStack> stacks) {
        if (recipe.getManaToConsume() > this.getCurrentMana()) return false;
        Item catalystItem = (this.checkWithCatalyst && !this.inventory.getStackInSlot(0).isEmpty()) ? this.inventory.getStackInSlot(0).getItem() : null;
        Block catalyst = catalystItem == null ? null : Block.byItem(catalystItem);
        if (catalyst == null && recipe.getRecipeCatalyst() != null) {
            return false;
        } else if (catalyst != null && (recipe.getRecipeCatalyst() == null || !recipe.getRecipeCatalyst().test(catalyst.defaultBlockState()))) {
            return false;
        }
        return super.matchRecipe(recipe, stacks);
    }

    @Override
    protected void onCrafted(IManaInfusionRecipe recipe) {
        this.cooldown = Math.max(1, LibXServerConfig.WorkingDurationMultiplier.mechanicalManaPool);
        this.receiveMana(-recipe.getManaToConsume());
    }

    @Override
    public int getComparatorOutput() {
        if (this.inventory.getStackInSlot(0).isEmpty()) return 0;
        Item item = this.inventory.getStackInSlot(0).getItem();
        if (CATALYSTS.contains(item)) {
            return Mth.clamp(1 + CATALYSTS.indexOf(item), 0, 15);
        } else {
            return 0;
        }
    }

    @Override
    public void receiveMana(int i) {
        if (this.inventory.getStackInSlot(0).getItem() == ModBlocks.manaVoid.asItem()) {
            super.receiveMana(Math.min(i, this.getAvailableSpaceForMana()));
        } else {
            super.receiveMana(i);
        }
    }

    @Override
    public boolean isFull() {
        return this.inventory.getStackInSlot(0).getItem() != ModBlocks.manaVoid.asItem() && super.isFull();
    }

    @Override
    public int getAvailableSpaceForMana() {
        return this.inventory.getStackInSlot(0).getItem() == ModBlocks.manaVoid.asItem() ? this.getManaCap() : super.getAvailableSpaceForMana();
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.cooldown = nbt.getInt(TileTags.COOLDOWN);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt(TileTags.COOLDOWN, this.cooldown);
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        if (this.level != null && !this.level.isClientSide) return;
        this.cooldown = nbt.getInt(TileTags.COOLDOWN);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        nbt.putInt(TileTags.COOLDOWN, this.cooldown);
        return nbt;
    }
}
