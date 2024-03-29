package de.melanx.botanicalmachinery.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.util.List;

public class GhostItemRenderer {

    public static void renderGhostItem(List<ItemStack> stacks, GuiGraphics guiGraphics, int x, int y) {
        if (stacks.isEmpty()) return;
        ItemStack stack = stacks.get((((ClientTickHandler.ticksInGame / 20) % stacks.size()) + stacks.size()) % stacks.size());
        renderGhostItem(stack, guiGraphics, x, y);
    }

    public static void renderGhostItem(ItemStack stack, GuiGraphics guiGraphics, int x, int y) {
        if (stack.isEmpty()) return;
        guiGraphics.renderFakeItem(stack, x, y);
        RenderSystem.depthFunc(GL11.GL_GREATER);
        guiGraphics.fill(x, y, x + 16, y + 16, 0x30ffffff);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }
}
