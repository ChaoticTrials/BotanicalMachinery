package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.inventory.ItemStackHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;

public class TileIndustrialAgglomerationFactory extends TileBase {
    private static final int recipeCost = TilePool.MAX_MANA / 2;
    private static final int workingDuration = 100;
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4);
    private final LazyOptional<IItemHandlerModifiable> handler = ItemStackHandlerWrapper.create(this.inventory);
    private int progress;
    private boolean recipe;

    private static final String TAG_PROGRESS = "progress";

    public TileIndustrialAgglomerationFactory() {
        super(Registration.TILE_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), 1_000_000);
        this.inventory.setOutputSlots(3);
        this.inventory.setSlotValidator(this::canInsertStack);
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canInsertStack(int slot, ItemStack stack) {
        if ((slot == 0 && !ModTags.Items.INGOTS_MANASTEEL.contains(stack.getItem())) ||
                (slot == 1 && !ModTags.Items.GEMS_MANA_DIAMOND.contains(stack.getItem())) ||
                (slot == 2 && ModItems.manaPearl != stack.getItem()))
                return false;
        return true;
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
            ItemStack manasteel = this.inventory.getStackInSlot(0);
            ItemStack manadiamond = this.inventory.getStackInSlot(1);
            ItemStack manapearl = this.inventory.getStackInSlot(2);
            ItemStack output = this.inventory.getStackInSlot(3);
            if (!manasteel.isEmpty() && !manadiamond.isEmpty() &&
                    !manapearl.isEmpty() && output.getCount() < 64) {
                this.recipe = true;
                if (this.getCurrentMana() >= recipeCost || this.progress > 0 && this.progress <= workingDuration) {
                    ++this.progress;
                    this.receiveMana(-(recipeCost / workingDuration));
                    if (this.progress >= workingDuration) {
                        manasteel.shrink(1);
                        manadiamond.shrink(1);
                        manapearl.shrink(1);
                        this.inventory.insertItemSuper(3, new ItemStack(ModItems.terrasteel), false);
                        this.recipe = false;
                    }
                    this.markDirty();
                }
            }
            if (!this.recipe && this.progress > 0) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            } else if (this.recipe) {
                this.recipe = false;
            }
        }
    }

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap) {
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap);
    }

    public int getProgress() {
        return this.progress;
    }
}
