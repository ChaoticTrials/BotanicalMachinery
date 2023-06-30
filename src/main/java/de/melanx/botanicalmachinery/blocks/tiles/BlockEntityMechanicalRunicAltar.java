package de.melanx.botanicalmachinery.blocks.tiles;

import com.google.common.collect.Range;
import com.google.common.collect.Streams;
import de.melanx.botanicalmachinery.blocks.base.WorkingTile;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.moddingx.libx.crafting.recipe.RecipeHelper;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import vazkii.botania.api.recipe.RunicAltarRecipe;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.lib.BotaniaTags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockEntityMechanicalRunicAltar extends WorkingTile<RunicAltarRecipe> {

    public static final int MAX_MANA_PER_TICK = 100;

    private final BaseItemStackHandler inventory;

    private final List<Integer> slotsUsed = new ArrayList<>();

    public BlockEntityMechanicalRunicAltar(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, BotaniaRecipeTypes.RUNE_TYPE, pos, state, LibXServerConfig.MaxManaCapacity.mechanicalRunicAltar, 1, 17);
        this.inventory = BaseItemStackHandler.builder(33)
                .validator(stack -> stack.getItem() == BotaniaBlocks.livingrock.asItem(), 0)
                .validator(stack -> this.level != null && RecipeHelper.isItemValidInput(this.level.getRecipeManager(), BotaniaRecipeTypes.RUNE_TYPE, stack), Range.closedOpen(1, 17))
                .output(Range.closedOpen(17, 33))
                .contentsChanged(() -> {
                    this.setChanged();
                    this.setDispatchable();
                    this.needsRecipeUpdate();
                })
                .build();
    }

    @Override
    protected Predicate<Integer> getExtracts(Supplier<IItemHandlerModifiable> inventory) {
        return slot -> slot >= 17 && slot <= 33;
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            this.runRecipeTick(this.slotsUsed::clear, (stack, slot) -> this.slotsUsed.add(slot), (stack, slot) -> {
            });
        } else if (this.level != null && LibXClientConfig.AdvancedRendering.all && LibXClientConfig.AdvancedRendering.industrialAgglomerationFactory) {
            if (this.getMaxProgress() > 0 && this.getProgress() >= (this.getMaxProgress() - (5 * this.getMaxManaPerTick()))) {
                for (int i = 0; i < 5; ++i) {
                    SparkleParticleData data = SparkleParticleData.sparkle(this.level.random.nextFloat(), this.level.random.nextFloat(), this.level.random.nextFloat(), this.level.random.nextFloat(), 10);
                    this.level.addParticle(data, this.worldPosition.getX() + 0.3 + (this.level.random.nextDouble() * 0.4), this.worldPosition.getY() + 0.7, this.worldPosition.getZ() + 0.3 + (this.level.random.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    protected boolean canMatchRecipes() {
        return !this.inventory.getStackInSlot(0).isEmpty();
    }

    @Override
    protected List<ItemStack> resultItems(RunicAltarRecipe recipe, List<ItemStack> stacks) {
        return Streams.concat(
                stacks.stream().filter(s -> s.is(BotaniaTags.Items.RUNES)).map(ItemStack::copy),
                super.resultItems(recipe, stacks).stream()
        ).toList();
    }

    @Override
    protected void onCrafted(RunicAltarRecipe recipe) {
        this.inventory.extractItem(0, 1, false);
    }

    @Override
    protected int getMaxProgress(RunicAltarRecipe recipe) {
        return recipe.getManaUsage();
    }

    @Override
    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK * LibXServerConfig.WorkingDurationMultiplier.mechanicalRunicAltar;
    }

    public boolean isSlotUsedCurrently(int slot) {
        return this.slotsUsed.contains(slot);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.slotsUsed.clear();
        this.slotsUsed.addAll(Arrays.stream(nbt.getIntArray(TileTags.SLOTS_USED)).boxed().toList());
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putIntArray(TileTags.SLOTS_USED, this.slotsUsed);
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        if (this.level != null && !this.level.isClientSide) return;
        this.slotsUsed.clear();
        this.slotsUsed.addAll(Arrays.stream(nbt.getIntArray(TileTags.SLOTS_USED)).boxed().toList());
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        nbt.putIntArray(TileTags.SLOTS_USED, this.slotsUsed);
        return nbt;
    }
}
