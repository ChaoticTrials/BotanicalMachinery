package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.util.ResourceLocation;

public class LibResources {

    private static final String GUI_PREFIX = "textures/gui/";

    public static final ResourceLocation HUD = loc(GUI_PREFIX + "misc/hud");
    public static final ResourceLocation MANA_BAR = loc(GUI_PREFIX + "misc/mana_bar");
    public static final ResourceLocation MANA_BAR_CURRENT = loc(GUI_PREFIX + "misc/current_mana");
    public static final ResourceLocation MECHANICAL_MANA_POOL_GUI = loc(GUI_PREFIX + LibNames.MECHANICAL_MANA_POOL);
    public static final ResourceLocation INDUSTRIAL_AGGLOMERATION_FACTORY_GUI = loc(GUI_PREFIX + LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY);
    public static final ResourceLocation MECHANICAL_RUNIC_ALTAR_GUI = loc(GUI_PREFIX + LibNames.MECHANICAL_RUNIC_ALTAR);

    private static ResourceLocation loc(String id) {
        return new ResourceLocation(BotanicalMachinery.MODID, id + ".png");
    }

}
