package de.melanx.botanicalmachinery.blocks.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IThrottledPacket;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.block.tile.mana.TilePool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileManaBlock extends TileMod implements IManaPool, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickableTileEntity {

    public static boolean enoughMana = true;
    private final IItemHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    public int manaCap = 10_000_000;
    private int mana;

    public TileManaBlock() {
        super(Registration.TILE_MANA_BLOCK.get());
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUD(Minecraft mc) {
        ItemStack block = new ItemStack(getBlockState().getBlock());
        String name = block.getDisplayName().getString();
        int color = 0x4444FF;
        HUDHandler.drawSimpleManaHUD(color, getCurrentMana(), manaCap, name);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.textureManager.bindTexture(HUDHandler.manaBar);

        RenderSystem.disableLighting();
        RenderSystem.disableBlend();
    }

    private IItemHandler createHandler() {
        return new ItemStackHandler(64) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getDamage() <= 0 && stack.getItem() != Blocks.SPAWNER.asItem();
            }

//            @Nonnull
//            @Override
//            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//                if (stack.getDamage() > 0 && slot != 0) {
//                    return stack;
//                }
//                return super.insertItem(slot, stack, simulate);
//            }
//
//            @Nonnull
//            @Override
//            public ItemStack extractItem(int slot, int amount, boolean simulate) {
//                if (slot < 2 && simulate) return ItemStack.EMPTY;
//                return super.extractItem(slot, amount, simulate);
//            }
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
        if (world != null) {
            ItemStack stack = itemHandler.getStackInSlot(1);
            ItemStack cat = itemHandler.getStackInSlot(0);
            IManaInfusionRecipe recipe = getMatchingRecipe(stack, cat);
            if (!world.isRemote) {
                enoughMana = true;
                if (recipe != null) {
                    int mana = recipe.getManaToConsume();
                    if (getCurrentMana() >= mana && (itemHandler.getStackInSlot(2).isEmpty() || (recipe.getRecipeOutput().getItem() == itemHandler.getStackInSlot(2).getItem() && itemHandler.getStackInSlot(2).getMaxStackSize() > itemHandler.getStackInSlot(2).getCount()))) {
                        receiveMana(-mana);
                        stack.shrink(1);

                        ItemStack output = recipe.getRecipeOutput().copy();
                        itemHandler.insertItem(2, output, false);
                        markDirty();
                    } else {
                        enoughMana = false;
                    }
                } else {
                    if (!stack.isEmpty()) enoughMana = false;
                }
            }
        }
    }

    @Override
    public String getInputKey() {
        return null;
    }

    @Override
    public String getOutputKey() {
        return null;
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

    @Override
    public void markDispatchable() {

    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return false;
    }

    @Override
    public void attachSpark(ISparkEntity iSparkEntity) {

    }

    @Override
    public int getAvailableSpaceForMana() {
        return 0;
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void receiveMana(int i) {

    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return false;
    }

    @Override
    public int getCurrentMana() {
        return 1000000;
    }
}
