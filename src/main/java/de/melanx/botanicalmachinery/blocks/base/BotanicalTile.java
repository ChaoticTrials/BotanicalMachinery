package de.melanx.botanicalmachinery.blocks.base;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.core.TileTags;
import io.github.noeppi_noeppi.libx.base.tile.BlockEntityBase;
import io.github.noeppi_noeppi.libx.base.tile.TickableBlock;
import io.github.noeppi_noeppi.libx.capability.ItemCapabilities;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.inventory.IAdvancedItemHandlerModifiable;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.item.ISparkEntity;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.tile.mana.IThrottledPacket;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@OnlyIn(value = Dist.CLIENT, _interface = IWandHUD.class)
public abstract class BotanicalTile extends BlockEntityBase implements IManaPool, IKeyLocked, ISparkAttachable, IThrottledPacket, IWandHUD, TickableBlock {

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
        return ItemCapabilities.create(inventory);
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
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
        HUDHandler.drawSimpleManaHUD(poseStack, color, this.getCurrentMana(), this.getManaCap(), name);

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
    public void attachSpark(IManaSpark entity) {

    }

    @Override
    public int getAvailableSpaceForMana() {
        if (!this.actAsMana()) return 0;
        return Math.max(Math.max(0, this.getManaCap() - this.getCurrentMana()), 0);
    }


    @Override
    public IManaSpark getAttachedSpark() {
        if (!this.actAsMana()) return null;
        //noinspection ConstantConditions
        List<Entity> sparks = this.level.getEntitiesOfClass(Entity.class, new AABB(this.worldPosition.above(), this.worldPosition.above().offset(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        if (sparks.size() == 1) {
            Entity entity = sparks.get(0);
            return (IManaSpark) entity;
        }
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return !this.actAsMana();
    }

    @Override
    public boolean isFull() {
        return this.getCurrentMana() >= this.getManaCap();
    }

    @Override
    public void receiveMana(int i) {
        int old = this.getCurrentMana();
        this.mana = Math.max(0, Math.min(this.getCurrentMana() + i, this.getManaCap()));
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

    public int getManaCap() {
        return this.manaCap;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public DyeColor getColor() {
        return null;
    }

    @Override
    public void setColor(DyeColor dyeColor) {
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
