package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;

public class TileIndustrialAgglomerationFactory extends TileBase implements IWorkingTile {

    public static final int RECIPE_COST = TilePool.MAX_MANA / 2;
    public static final int MAX_MANA_PER_TICK = RECIPE_COST / 100;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4, slot -> this.sendPacket = true, this::isValidStack);
    private int progress;
    private boolean recipe;

    public TileIndustrialAgglomerationFactory(TileEntityType<?> type) {
        super(type, ServerConfig.capacityAgglomerationFactory.get());
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
        return (slot != 0 || ModTags.Items.INGOTS_MANASTEEL.contains(stack.getItem())) &&
                (slot != 1 || ModTags.Items.GEMS_MANA_DIAMOND.contains(stack.getItem())) &&
                (slot != 2 || ModItems.manaPearl == stack.getItem());
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT cmp) {
        super.read(state, cmp);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT cmp) {
        cmp.putInt(TileTags.PROGRESS, this.progress);
        return super.write(cmp);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT cmp) {
        if (world != null && !world.isRemote) return;
        super.handleUpdateTag(state, cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        if (world != null && world.isRemote) return super.getUpdateTag();
        CompoundNBT cmp = super.getUpdateTag();
        cmp.putInt(TileTags.PROGRESS, this.progress);
        return cmp;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world != null && !this.world.isRemote) {
            ItemStack manasteel = this.inventory.getStackInSlot(0);
            ItemStack manadiamond = this.inventory.getStackInSlot(1);
            ItemStack manapearl = this.inventory.getStackInSlot(2);
            ItemStack output = this.inventory.getStackInSlot(3);
            if (!manasteel.isEmpty() && !manadiamond.isEmpty() &&
                    !manapearl.isEmpty() && output.getCount() < 64) {
                this.recipe = true;
                int manaTransfer = Math.min(this.mana, Math.min(this.getMaxManaPerTick(), this.getMaxProgress() - this.progress));
                this.progress += manaTransfer;
                this.receiveMana(-manaTransfer);
                if (this.progress >= this.getMaxProgress()) {
                    manasteel.shrink(1);
                    manadiamond.shrink(1);
                    manapearl.shrink(1);
                    this.inventory.getUnrestricted().insertItem(3, new ItemStack(ModItems.terrasteel), false);
                    this.recipe = false;
                }
                this.markDirty();
            }
            if (!this.recipe && this.progress > 0) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            } else if (this.recipe) {
                this.recipe = false;
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
        return RECIPE_COST;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK / ServerConfig.multiplierAgglomerationFactory.get();
    }
}
