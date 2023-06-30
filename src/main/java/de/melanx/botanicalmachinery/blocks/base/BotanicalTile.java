package de.melanx.botanicalmachinery.blocks.base;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.core.TileTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.opengl.GL11;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;
import org.moddingx.libx.capability.ItemCapabilities;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.inventory.IAdvancedItemHandlerModifiable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.item.SparkEntity;
import vazkii.botania.api.mana.KeyLocked;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.block_entity.mana.ThrottledPacket;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

@OnlyIn(value = Dist.CLIENT, _interface = WandHUD.class)
public abstract class BotanicalTile extends BlockEntityBase implements ManaPool, KeyLocked, SparkAttachable, ThrottledPacket, WandHUD, TickingBlock {

    private int mana;
    private final int manaCap;
    private String inputKey = "";
    private String outputKey = "";

    private final LazyOptional<IAdvancedItemHandlerModifiable> capability = this.createCap(this::getInventory);

    public BotanicalTile(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state, int manaCap) {
        super(blockEntityTypeIn, pos, state);
        this.manaCap = manaCap;
    }

    /**
     * This can be used to add canExtract or canInsert to the wrapper used as capability. You may not call the supplier
     * now. Always use IItemHandlerModifiable.createLazy. You may call the supplier inside the canExtract and canInsert
     * lambda.
     */
    protected LazyOptional<IAdvancedItemHandlerModifiable> createCap(Supplier<IItemHandlerModifiable> inventory) {
        return ItemCapabilities.create(inventory, this.getExtracts(inventory), this.getInserts(inventory));
    }

    protected abstract Predicate<Integer> getExtracts(Supplier<IItemHandlerModifiable> inventory);

    protected BiPredicate<Integer, ItemStack> getInserts(Supplier<IItemHandlerModifiable> inventory) {
        return null;
    }

    @Nonnull
    public abstract BaseItemStackHandler getInventory();

    public abstract int getComparatorOutput();

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap, Direction direction) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.capability.cast();
        } else if (!this.remove && this.actAsMana() && (cap == BotaniaForgeCapabilities.MANA_RECEIVER || cap == BotaniaForgeCapabilities.SPARK_ATTACHABLE)) {
            return LazyOptional.of(() -> this).cast();
        }
        return DistExecutor.unsafeRunForDist(() -> () -> {
            if (!this.remove && this.actAsMana() && cap == BotaniaForgeClientCapabilities.WAND_HUD) {
                return LazyOptional.of(() -> this).cast();
            }
            return super.getCapability(cap, direction);
        }, () -> () -> super.getCapability(cap, direction));
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.getInventory().deserializeNBT(tag.getCompound(TileTags.INVENTORY));
        this.mana = tag.getInt(TileTags.MANA);
        if (tag.contains(TileTags.INPUT_KEY)) this.inputKey = tag.getString(TileTags.INPUT_KEY);
        if (tag.contains(TileTags.OUTPUT_KEY)) this.outputKey = tag.getString(TileTags.OUTPUT_KEY);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put(TileTags.INVENTORY, this.getInventory().serializeNBT());
        nbt.putInt(TileTags.MANA, this.getCurrentMana());
        nbt.putString(TileTags.INPUT_KEY, this.inputKey);
        nbt.putString(TileTags.OUTPUT_KEY, this.outputKey);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (this.level != null && !this.level.isClientSide) return;
        this.getInventory().deserializeNBT(tag.getCompound(TileTags.INVENTORY));
        this.mana = tag.getInt(TileTags.MANA);
        if (tag.contains(TileTags.INPUT_KEY)) this.inputKey = tag.getString(TileTags.INPUT_KEY);
        if (tag.contains(TileTags.OUTPUT_KEY)) this.outputKey = tag.getString(TileTags.OUTPUT_KEY);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if (this.level != null && this.level.isClientSide) return super.getUpdateTag();
        CompoundTag nbt = super.getUpdateTag();
        nbt.put(TileTags.INVENTORY, this.getInventory().serializeNBT());
        nbt.putInt(TileTags.MANA, this.getCurrentMana());
        nbt.putString(TileTags.INPUT_KEY, this.inputKey);
        nbt.putString(TileTags.OUTPUT_KEY, this.outputKey);
        return nbt;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUD(PoseStack poseStack, Minecraft minecraft) {
        ItemStack block = new ItemStack(this.getBlockState().getBlock());
        String name = block.getHoverName().getString();
        int color = 0x4444FF;
        HUDHandler.drawSimpleManaHUD(poseStack, color, this.getCurrentMana(), this.getMaxMana(), name);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShaderTexture(0, HUDHandler.manaBar);

        RenderSystem.disableBlend();
    }

    @Override
    public String getInputKey() {
        return this.inputKey;
    }

    @Override
    public String getOutputKey() {
        return this.outputKey;
    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return this.actAsMana();
    }

    @Override
    public int getAvailableSpaceForMana() {
        if (!this.actAsMana()) return 0;
        return Math.max(Math.max(0, this.getMaxMana() - this.getCurrentMana()), 0);
    }


    @Override
    public ManaSpark getAttachedSpark() {
        if (!this.actAsMana()) return null;
        //noinspection ConstantConditions
        List<Entity> sparks = this.level.getEntitiesOfClass(Entity.class, new AABB(this.worldPosition.above(), this.worldPosition.above().offset(1, 1, 1)), Predicates.instanceOf(SparkEntity.class));
        if (sparks.size() == 1) {
            Entity entity = sparks.get(0);
            return (ManaSpark) entity;
        }
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return !this.actAsMana();
    }

    @Override
    public boolean isFull() {
        return this.getCurrentMana() >= this.getMaxMana();
    }

    @Override
    public void receiveMana(int i) {
        int old = this.getCurrentMana();
        this.mana = Math.max(0, Math.min(this.getCurrentMana() + i, this.getMaxMana()));
        if (old != this.getCurrentMana()) {
            this.setChanged();
            this.setDispatchable();
        }
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public int getMaxMana() {
        return this.manaCap;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public Optional<DyeColor> getColor() {
        return Optional.empty();
    }

    @Override
    public void setColor(Optional<DyeColor> color) {
        // unused
    }

    @Override
    public void markDispatchable() {
        this.setDispatchable();
    }

    @Override
    public Level getManaReceiverLevel() {
        return this.getLevel();
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return this.getBlockPos();
    }
    
    public boolean actAsMana() {
        return true;
    }
}
