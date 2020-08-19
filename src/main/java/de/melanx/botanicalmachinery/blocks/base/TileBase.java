package de.melanx.botanicalmachinery.blocks.base;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IThrottledPacket;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public abstract class TileBase extends TileMod implements IManaPool, IManaMachineTile, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickableTileEntity {

    public static final String TAG_INV = "inv";
    public static final String TAG_MANA = "mana";
    public static final String TAG_MANA_CAP = "manaCap";
    public static final String TAG_INPUT_KEY = "inputKey";
    public static final String TAG_OUTPUT_KEY = "outputKey";

    public int mana;
    private int manaCap;
    public String inputKey = "";
    public final String outputKey = "";

    public boolean sendPacket = false;

    private final LazyOptional<IItemHandlerModifiable> handler = this.createHandler(this::getInventory);

    public TileBase(TileEntityType<?> tileEntityTypeIn, int manaCap) {
        super(tileEntityTypeIn);
        this.manaCap = manaCap;
    }

    /**
     * This can be used to add canExtract or canInsert to the wrapper used as capability. You may not call the supplier
     * now. Always use IItemHandlerModifiable.createLazy. You may call the supplier inside the canExtract and canInsert
     * lambda.
     */
    protected LazyOptional<IItemHandlerModifiable> createHandler(Supplier<IItemHandlerModifiable> inventory) {
        return ItemStackHandlerWrapper.createLazy(inventory);
    }

    @Nonnull
    public abstract BaseItemStackHandler getInventory();

    public abstract boolean isValidStack(int slot, ItemStack stack);

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> cap, Direction direction) {
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        cmp.put(TAG_INV, this.getInventory().serializeNBT());
        cmp.putInt(TAG_MANA, this.getCurrentMana());
        cmp.putInt(TAG_MANA_CAP, this.getManaCap());
        cmp.putString(TAG_INPUT_KEY, this.inputKey);
        cmp.putString(TAG_OUTPUT_KEY, this.outputKey);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        this.getInventory().deserializeNBT(cmp.getCompound(TAG_INV));
        this.mana = cmp.getInt(TAG_MANA);
        this.manaCap = cmp.getInt(TAG_MANA_CAP);

        if (cmp.contains(TAG_INPUT_KEY)) this.inputKey = cmp.getString(TAG_INPUT_KEY);
        if (cmp.contains(TAG_OUTPUT_KEY)) this.inputKey = cmp.getString(TAG_OUTPUT_KEY);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUD(Minecraft mc) {
        ItemStack block = new ItemStack(this.getBlockState().getBlock());
        String name = block.getDisplayName().getString();
        int color = 0x4444FF;
        HUDHandler.drawSimpleManaHUD(color, this.getCurrentMana(), this.getManaCap(), name);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.textureManager.bindTexture(HUDHandler.manaBar);

        RenderSystem.disableLighting();
        RenderSystem.disableBlend();
    }

    @Override
    public void tick() {
        if (this.world != null) {
            if (this.sendPacket) {
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
                this.sendPacket = false;
            }
        }
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
    public void markDispatchable() {
        this.sendPacket = true;
    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity iSparkEntity) {
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(Math.max(0, this.getManaCap() - this.getCurrentMana()), 0);
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        @SuppressWarnings("ConstantConditions")
        List<Entity> sparks = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.pos.up(), this.pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        if (sparks.size() == 1) {
            Entity entity = sparks.get(0);
            return (ISparkEntity) entity;
        }
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public boolean isFull() {
        return this.getCurrentMana() >= this.getManaCap();
    }

    @Override
    public void receiveMana(int i) {
        int old = this.mana;
        this.mana = Math.max(0, Math.min(this.getCurrentMana() + i, this.getManaCap()));
        if (old != this.mana) {
            this.markDirty();
            this.markDispatchable();
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
}
