package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBattery;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class ScreenManaBattery extends ScreenBase<ContainerManaBattery> {

    boolean slot1Locked = false;
    boolean slot2Locked = false;

    private int xB1;
    private int yB1;
    private int xB2;
    private int yB2;

    public ScreenManaBattery(ContainerManaBattery container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void init(@Nonnull Minecraft mc, int x, int y) {
        super.init(mc, x, y);
        this.xB1 = this.relX + 51;
        this.yB1 = this.relY + 49;
        this.xB2 = this.relX + 105;
        this.yB2 = this.relY + 49;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MANA_BATTERY_GUI, 81, 37);

        BlockPos tilePos = this.container.getPos();
        TileManaBattery tile = (TileManaBattery) this.container.getWorld().getTileEntity(tilePos);
        if (tile == null) return;

        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(LibResources.MANA_BATTERY_GUI);
        if (mouseX >= this.xB1 && mouseX < this.xB1 + 20 && mouseY >= this.yB1 && mouseY < this.yB1 + 20) {
            this.blit(this.xB1, this.yB1, 20, tile.isSlot1Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        } else {
            this.blit(this.xB1, this.yB1, 0, tile.isSlot2Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        }

        if (mouseX >= this.xB2 && mouseX < this.xB2 + 20 && mouseY >= this.yB2 && mouseY < this.yB2 + 20) {
            this.blit(this.xB2, this.yB2, 20, tile.isSlot1Locked() ? this.ySize+ 20 : this.ySize, 20, 20);
        } else {
            this.blit(this.xB2, this.yB2, 0, tile.isSlot2Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        if (clickedButton == 0) {
            if (mouseX >= this.xB1 && mouseX < this.xB1 + 20 && mouseY >= this.yB1 && mouseY < this.yB1 + 20) {
                this.slot1Locked = !this.slot1Locked;
            }
            if (mouseX >= this.xB2 && mouseX < this.xB2 + 20 && mouseY >= this.yB2 && mouseY < this.yB2 + 20) {
                this.slot2Locked = !this.slot2Locked;
            }
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }
}
