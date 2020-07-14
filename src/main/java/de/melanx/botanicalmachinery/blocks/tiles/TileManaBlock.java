package de.melanx.botanicalmachinery.blocks.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IThrottledPacket;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;

public class TileManaBlock extends TileMod implements IManaPool, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickableTileEntity {

    public int manaCap = 1000000;
    private boolean outputting = false;

    public TileManaBlock() {
        super(Registration.TILE_MANA_BLOCK.get());
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUD(MatrixStack ms, Minecraft mc) {
        ItemStack block = new ItemStack(getBlockState().getBlock());
        String name = block.getDisplayName().getString();
        int color = 0x4444FF;
        BotaniaAPIClient.instance().drawSimpleManaHUD(ms, color, getCurrentMana(), manaCap, name);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.textureManager.bindTexture(HUDHandler.manaBar);

        RenderSystem.disableLighting();
        RenderSystem.disableBlend();
    }



    @Override
    public void tick() {

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
