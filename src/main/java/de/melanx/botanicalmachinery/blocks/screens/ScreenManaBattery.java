package de.melanx.botanicalmachinery.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuManaBattery;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ScreenManaBattery extends ScreenBase<ContainerMenuManaBattery> {

    private int xB1;
    private int yB1;
    private int xB2;
    private int yB2;

    public ScreenManaBattery(ContainerMenuManaBattery menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
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
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(poseStack, LibResources.MANA_BATTERY_GUI);

        BlockPos tilePos = this.menu.getPos();
        BlockEntityManaBattery blockEntity = (BlockEntityManaBattery) this.menu.getLevel().getBlockEntity(tilePos);
        if (blockEntity == null) return;

        RenderSystem.setShaderTexture(0, LibResources.MANA_BATTERY_GUI);
        if (mouseX >= this.xB1 && mouseX < this.xB1 + 20 && mouseY >= this.yB1 && mouseY < this.yB1 + 20) {
            this.blit(poseStack, this.xB1, this.yB1, 20, blockEntity.isSlot1Locked() ? this.imageHeight + 20 : this.imageHeight, 20, 20);
        } else {
            this.blit(poseStack, this.xB1, this.yB1, 0, blockEntity.isSlot1Locked() ? this.imageHeight + 20 : this.imageHeight, 20, 20);
        }

        if (mouseX >= this.xB2 && mouseX < this.xB2 + 20 && mouseY >= this.yB2 && mouseY < this.yB2 + 20) {
            this.blit(poseStack, this.xB2, this.yB2, 20, blockEntity.isSlot2Locked() ? this.imageHeight + 20 : this.imageHeight, 20, 20);
        } else {
            this.blit(poseStack, this.xB2, this.yB2, 0, blockEntity.isSlot2Locked() ? this.imageHeight + 20 : this.imageHeight, 20, 20);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            BlockPos tilePos = this.menu.getPos();
            BlockEntityManaBattery blockEntity = (BlockEntityManaBattery) this.menu.getLevel().getBlockEntity(tilePos);
            if (blockEntity == null) return super.mouseClicked(mouseX, mouseY, button);

            if (mouseX >= this.xB1 && mouseX < this.xB1 + 20 && mouseY >= this.yB1 && mouseY < this.yB1 + 20) {
                blockEntity.setSlot1Locked(!blockEntity.isSlot1Locked());
                BotanicalMachinery.getNetwork().updateLockedState(blockEntity);
                SoundHelper.playSound(SoundEvents.UI_BUTTON_CLICK);
            }

            if (mouseX >= this.xB2 && mouseX < this.xB2 + 20 && mouseY >= this.yB2 && mouseY < this.yB2 + 20) {
                blockEntity.setSlot2Locked(!blockEntity.isSlot2Locked());
                BotanicalMachinery.getNetwork().updateLockedState(blockEntity);
                SoundHelper.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
