package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.WorkingTile;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.recipe.ITerraPlateRecipe;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.crafting.ModRecipeTypes;

import javax.annotation.Nonnull;

public class BlockEntityIndustrialAgglomerationFactory extends WorkingTile<ITerraPlateRecipe> {

    public static final int MAX_MANA_PER_TICK = 5000;

    private final BaseItemStackHandler inventory;

    public BlockEntityIndustrialAgglomerationFactory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, ModRecipeTypes.TERRA_PLATE_TYPE, pos, state, LibXServerConfig.MaxManaCapacity.industrialAgglomerationFactory, 0, 3);
        this.inventory = BaseItemStackHandler.builder(4)
                .validator(stack -> this.level != null && RecipeHelper.isItemValidInput(this.level.getRecipeManager(), ModRecipeTypes.TERRA_PLATE_TYPE, stack), 0, 1, 2)
                .output(3)
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
        } else if (this.level != null && LibXClientConfig.AdvancedRendering.all && LibXClientConfig.AdvancedRendering.industrialAgglomerationFactory) {
            if (this.getProgress() > 0) {
                double time = this.getProgress() / (double) this.getMaxProgress();
                if (time < 0.8) {
                    time = time * 1.25;
                    double y = this.worldPosition.getY() + 6 / 16d + ((5 / 16d) * time);
                    double x1 = this.worldPosition.getX() + 0.2 + (0.3 * time);
                    double x2 = this.worldPosition.getX() + 0.8 - (0.3 * time);
                    double z1 = this.worldPosition.getZ() + 0.2 + (0.3 * time);
                    double z2 = this.worldPosition.getZ() + 0.8 - (0.3 * time);
                    WispParticleData data = WispParticleData.wisp(0.1f, 0, (float) time, (float) (1 - time), 1);
                    this.level.addParticle(data, x1, y, z1, 0, 0, 0);
                    this.level.addParticle(data, x1, y, z2, 0, 0, 0);
                    this.level.addParticle(data, x2, y, z1, 0, 0, 0);
                    this.level.addParticle(data, x2, y, z2, 0, 0, 0);
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
    protected int getMaxProgress(ITerraPlateRecipe recipe) {
        return recipe.getMana();
    }

    @Override
    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK * LibXServerConfig.WorkingDurationMultiplier.industrialAgglomerationFactory;
    }
}
