package de.melanx.botanicalmachinery.gui;

import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.screen.Screen;

/*
 * This class is inspired by Cyclics EnergyBar
 */
public class ManaBar {

    private final Screen parent;
    private final int x = 154;
    private final int y = 8;
    public int capacity;
    private final int width = 16;
    private final int height = 62;
    public int guiLeft;
    public int guiTop;

    public ManaBar(Screen parent, int capacity) {
        this.parent = parent;
        this.capacity = capacity;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return guiLeft + x < mouseX && mouseX < guiLeft + x + width
                && guiTop + y < mouseY && mouseY < guiTop + y + height;
    }

    public void draw(float mana) {
        int relX;
        int relY;
        parent.getMinecraft().getTextureManager().bindTexture(LibResources.MANA_BAR);
        relX = guiLeft + x;
        relY = guiTop + y;
        Screen.blit(relX, relY, 0, 0, width, height, width, height);
        parent.getMinecraft().getTextureManager().bindTexture(LibResources.MANA_BAR_CURRENT);
        relX += 1;
        relY += height - 1;
        float pct = Math.min(mana / this.capacity, 1.0F);
        Screen.blit(relX, relY, 0, 0, width - 2, (int) -((height - 2) * pct), width - 2, height - 2);
    }

    public void renderHoveredToolTip(int mouseX, int mouseY, int mana) {
        if (this.isMouseOver(mouseX, mouseY)) {
            parent.renderTooltip(mana + "/" + this.capacity, mouseX, mouseY);
        }
    }
}
