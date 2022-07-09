package de.melanx.botanicalmachinery.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.util.List;

public class GhostItemRenderer {
    
    public static void renderGhostItem(List<ItemStack> stacks, PoseStack poseStack, int x, int y) {
        if (stacks.isEmpty()) return;
        ItemStack stack = stacks.get((((ClientTickHandler.ticksInGame / 20) % stacks.size()) + stacks.size()) % stacks.size());
        renderGhostItem(stack, poseStack, x, y);
    }
    
    public static void renderGhostItem(ItemStack stack, PoseStack poseStack, int x, int y) {
        if (stack.isEmpty()) return;
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(stack, x, y);
        RenderSystem.depthFunc(GL11.GL_GREATER);
        GuiComponent.fill(poseStack, x, y, x + 16, y + 16, 0x30ffffff);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }
}
