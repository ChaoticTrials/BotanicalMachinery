package de.melanx.botanicalmachinery.blocks.tiles;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.blocks.BlockManaBlock;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IThrottledPacket;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileManaBlock extends TileMod implements IManaPool, IManaMachineTile, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickableTileEntity {

    public boolean validRecipe = true;
    private final ItemStackHandler itemHandler = createHandler();

    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> itemHandler);
    public int manaCap = 10_000_000;
    private int mana;
    private String inputKey = "";
    private final String outputKey = "";

    private boolean sendPacket = false;

    private static final String TAG_INV = "inv";
    private static final String TAG_MANA = "mana";
    private static final String TAG_MANA_CAP = "manaCap";
    private static final String TAG_INPUT_KEY = "inputKey";
    private static final String TAG_OUTPUT_KEY = "outputKey";

    public TileManaBlock() {
        super(Registration.TILE_MANA_BLOCK.get());
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUD(Minecraft mc) {
        ItemStack block = new ItemStack(getBlockState().getBlock());
        String name = block.getDisplayName().getString();
        int color = 0x4444FF;
        HUDHandler.drawSimpleManaHUD(color, this.getCurrentMana(), this.getManaCap(), name);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.textureManager.bindTexture(HUDHandler.manaBar);

        RenderSystem.disableLighting();
        RenderSystem.disableBlend();
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(64) {

            @Override
            protected void onContentsChanged(int slot) {
                if (slot == 1) {
                    ItemStack stack = this.getStackInSlot(slot);
                    ItemStack cat = this.getStackInSlot(0);
                    IManaInfusionRecipe recipe = getMatchingRecipe(stack, cat);
                    if (recipe != null) {
                        validRecipe = recipe.getManaToConsume() <= getCurrentMana();
                    } else {
                        validRecipe = stack.isEmpty();
                    }
                }
                markDirty();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        cmp.put(TAG_INV, this.itemHandler.serializeNBT());
        cmp.putInt(TAG_MANA, this.mana);
        cmp.putInt(TAG_MANA_CAP, this.getManaCap());
        cmp.putString(TAG_INPUT_KEY, this.inputKey);
        cmp.putString(TAG_OUTPUT_KEY, this.outputKey);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        this.itemHandler.deserializeNBT(cmp.getCompound(TAG_INV));
        this.mana = cmp.getInt(TAG_MANA);
        this.manaCap = cmp.getInt(TAG_MANA_CAP);

        if (cmp.contains(TAG_INPUT_KEY)) this.inputKey = cmp.getString(TAG_INPUT_KEY);
        if (cmp.contains(TAG_OUTPUT_KEY)) this.inputKey = cmp.getString(TAG_OUTPUT_KEY);
    }

    public IManaInfusionRecipe getMatchingRecipe(@Nonnull ItemStack stack, @Nonnull ItemStack cat) {
        List<IManaInfusionRecipe> matchingNonCatRecipes = new ArrayList<>();
        List<IManaInfusionRecipe> matchingCatRecipes = new ArrayList<>();

        for (IManaInfusionRecipe recipe : TilePool.manaInfusionRecipes(world.getRecipeManager())) {
            if (recipe.matches(stack)) {
                if (recipe.getCatalyst() == null) {
                    matchingNonCatRecipes.add(recipe);
                } else if (recipe.getCatalyst().getBlock().asItem() == cat.getItem()) {
                    matchingCatRecipes.add(recipe);
                }
            }
        }

        // Recipes with matching catalyst take priority above recipes with no catalyst specified
        return !matchingCatRecipes.isEmpty() ? matchingCatRecipes.get(0) : !matchingNonCatRecipes.isEmpty() ? matchingNonCatRecipes.get(0) : null;
    }

    @Override
    public void tick() {
        if (!ManaNetworkHandler.instance.isPoolIn(this) && !isRemoved()) {
            ManaNetworkEvent.addCollector(this);
        }

        if (world != null) {
            ItemStack stack = itemHandler.getStackInSlot(1);
            ItemStack cat = itemHandler.getStackInSlot(0);
            IManaInfusionRecipe recipe = getMatchingRecipe(stack, cat);
            if (!world.isRemote) {
                if (recipe != null) {
                    int mana = recipe.getManaToConsume();
                    if (getCurrentMana() >= mana && (itemHandler.getStackInSlot(2).isEmpty() || (recipe.getRecipeOutput().getItem() == itemHandler.getStackInSlot(2).getItem() && itemHandler.getStackInSlot(2).getMaxStackSize() > itemHandler.getStackInSlot(2).getCount()))) {
                        receiveMana(-mana);
                        stack.shrink(1);

                        ItemStack output = recipe.getRecipeOutput().copy();
                        itemHandler.insertItem(2, output, false);
                        markDirty();
                    }
                }
            }
            if (sendPacket) {
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
                sendPacket = false;
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
        sendPacket = true;
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
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
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
            markDirty();
            markDispatchable();
        }
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return false;
    }

    @Override
    public int getCurrentMana() {
        return this.getBlockState().getBlock() instanceof BlockManaBlock ? this.mana : 0;
    }

    @Override
    public boolean hasValidRecipe() {
        return validRecipe;
    }

    @Override
    public int getManaCap() {
        return manaCap;
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
    }
}
