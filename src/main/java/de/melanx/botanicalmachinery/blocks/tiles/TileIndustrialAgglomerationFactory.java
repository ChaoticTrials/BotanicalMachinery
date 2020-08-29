package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

    public TileIndustrialAgglomerationFactory() {
        super(Registration.TILE_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), 1_000_000);
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
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);
        cmp.putInt(TileTags.PROGRESS, this.progress);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInt(TileTags.PROGRESS);
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
                    int manaTransfer = Math.min(this.mana, Math.min(MAX_MANA_PER_TICK, this.getMaxProgress() - this.progress));
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
        } else if (this.world != null) {
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
}
