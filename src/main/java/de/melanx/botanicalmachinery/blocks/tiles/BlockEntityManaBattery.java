package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.BlockManaBattery;
import de.melanx.botanicalmachinery.blocks.base.BotanicalTile;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.common.item.ItemBlackLotus;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockEntityManaBattery extends BotanicalTile {

    private static final int MANA_TRANSFER_RATE = 5000;
    private boolean slot1Locked;
    private boolean slot2Locked;

    private final BaseItemStackHandler inventory;

    public BlockEntityManaBattery(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, LibXServerConfig.MaxManaCapacity.manaBattery);
        this.inventory = BaseItemStackHandler.builder(2)
                .validator(stack -> !this.slot1Locked && stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve().map(manaItem -> manaItem.getMana() < manaItem.getMaxMana() && manaItem.canReceiveManaFromPool(this)).orElse(false), 0)
                .validator(stack -> !this.slot2Locked && stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve().map(manaItem -> manaItem.getMana() > 0 && manaItem.canExportManaToPool(this)).orElse(false), 1)
                .contentsChanged(() -> {
                    this.setChanged();
                    this.setDispatchable();
                })
                .build();
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public int getCurrentMana() {
        return ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.CREATIVE ? this.getManaCap() / 2 : super.getCurrentMana();
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            ItemStack minus = this.inventory.getStackInSlot(0);
            ItemStack plus = this.inventory.getStackInSlot(1);
            if (!minus.isEmpty()) {
                minus.getCapability(BotaniaForgeCapabilities.MANA_ITEM).ifPresent(manaItem -> {
                    if (manaItem.canReceiveManaFromPool(this)) {
                        int maxManaValue = ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.NORMAL ? MANA_TRANSFER_RATE : Integer.MAX_VALUE;
                        int manaValue = Math.min(maxManaValue, Math.min(this.getCurrentMana(), manaItem.getMaxMana() - manaItem.getMana()));
                        manaItem.addMana(manaValue);
                        this.receiveMana(-manaValue);
                        this.setChanged();
                        this.setDispatchable();
                    }
                });
            }
            if (!plus.isEmpty()) {
                plus.getCapability(BotaniaForgeCapabilities.MANA_ITEM).ifPresent(manaItem -> {
                    if (manaItem.canExportManaToPool(this)) {
                        int maxManaValue = ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.NORMAL ? MANA_TRANSFER_RATE : Integer.MAX_VALUE;
                        int manaValue = Math.min(maxManaValue, Math.min(this.getManaCap() - this.getCurrentMana(), manaItem.getMana()));
                        manaItem.addMana(-manaValue);
                        this.receiveMana(manaValue);
                        this.setChanged();
                        this.setDispatchable();
                    }
                });
                if (plus.getItem() instanceof ItemBlackLotus) {
                    int manaToTransfer = plus.getItem() == ModItems.blackerLotus ? 100000 : 8000;
                    if (this.getAvailableSpaceForMana() >= manaToTransfer) {
                        this.receiveMana(manaToTransfer);
                        ItemStack stack = this.inventory.getStackInSlot(1).copy();
                        stack.shrink(1);
                        this.inventory.setStackInSlot(1, stack);
                        this.setChanged();
                        this.setDispatchable();
                    }
                }
            }
            for (Direction direction : Direction.values()) {
                BlockEntity tile = this.level.getBlockEntity(this.getBlockPos().relative(direction));
                if (tile instanceof BotanicalTile offsetTile && offsetTile.actAsMana() && (((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.CREATIVE || !(tile instanceof BlockEntityManaBattery))) {
                    if (!offsetTile.isFull()) {
                        int maxManaValue = ((BlockManaBattery) this.getBlockState().getBlock()).variant == BlockManaBattery.Variant.NORMAL ? MANA_TRANSFER_RATE : Integer.MAX_VALUE;
                        int manaValue = Math.min(maxManaValue, Math.min(this.getCurrentMana(), offsetTile.getManaCap() - offsetTile.getCurrentMana()));
                        if (manaValue <= 0 && offsetTile instanceof BlockEntityMechanicalManaPool)
                            manaValue = Math.min(this.getCurrentMana(), MANA_TRANSFER_RATE);
                        this.receiveMana(-manaValue);
                        offsetTile.receiveMana(manaValue);
                        this.setChanged();
                        this.setDispatchable();
                    }
                }
            }
        }
    }

    public boolean isSlot1Locked() {
        return this.slot1Locked;
    }

    public boolean isSlot2Locked() {
        return this.slot2Locked;
    }

    public void setSlot1Locked(boolean slot1Locked) {
        // Do not remove the condition! (Because of packets)
        if (slot1Locked != this.slot1Locked) {
            this.slot1Locked = slot1Locked;
            this.setChanged();
        }
    }

    public void setSlot2Locked(boolean slot2Locked) {
        // Do not remove the condition! (Because of packets)
        if (slot2Locked != this.slot2Locked) {
            this.slot2Locked = slot2Locked;
            this.setChanged();
        }
    }

    @Override
    public int getComparatorOutput() {
        return (int) Math.round(this.getCurrentMana() / (double) this.getManaCap() * 15d);
    }

    @Override
    protected Predicate<Integer> getExtracts(Supplier<IItemHandlerModifiable> inventory) {
        return slot -> {
            ItemStack minus = inventory.get().getStackInSlot(0);
            ItemStack plus = inventory.get().getStackInSlot(1);
            if (slot == 0) {
                Optional<IManaItem> manaItem = minus.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
                if (manaItem.isEmpty()) return true;
                return manaItem.get().getMana() >= manaItem.get().getMaxMana();
            } else if (slot == 1 && plus.getItem() instanceof IManaItem) {
                Optional<IManaItem> manaItem = plus.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
                if (manaItem.isEmpty()) return true;
                return manaItem.get().getMana() <= 0;
            }
            return true;
        };
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.slot1Locked = nbt.getBoolean(TileTags.SLOT_1_LOCKED);
        this.slot2Locked = nbt.getBoolean(TileTags.SLOT_2_LOCKED);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean(TileTags.SLOT_1_LOCKED, this.slot1Locked);
        nbt.putBoolean(TileTags.SLOT_2_LOCKED, this.slot2Locked);
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        if (this.level != null && !this.level.isClientSide) return;
        this.slot1Locked = nbt.getBoolean(TileTags.SLOT_1_LOCKED);
        this.slot2Locked = nbt.getBoolean(TileTags.SLOT_2_LOCKED);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        nbt.putBoolean(TileTags.SLOT_1_LOCKED, this.slot1Locked);
        nbt.putBoolean(TileTags.SLOT_2_LOCKED, this.slot2Locked);
        return nbt;
    }
}
