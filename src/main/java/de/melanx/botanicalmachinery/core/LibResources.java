package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.util.ResourceLocation;

public class LibResources {

    private static final String GUI_PREFIX = "textures/gui/";

    public static final ResourceLocation HUD = loc(GUI_PREFIX + "misc/hud");
    public static final ResourceLocation MANA_BAR = loc(GUI_PREFIX + "misc/mana_bar");
    public static final ResourceLocation MANA_BAR_CURRENT = loc(GUI_PREFIX + "misc/current_mana");

    public static final ResourceLocation ALFHEIM_MARKET_GUI = gui(LibNames.ALFHEIM_MARKET);
    public static final ResourceLocation INDUSTRIAL_AGGLOMERATION_FACTORY_GUI = gui(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY);
    public static final ResourceLocation MANA_BATTERY_GUI = gui(LibNames.MANA_BATTERY);
    public static final ResourceLocation MECHANICAL_APOTHECARY_GUI = gui(LibNames.MECHANICAL_APOTHECARY);
    public static final ResourceLocation MECHANICAL_BREWERY_GUI = gui(LibNames.MECHANICAL_BREWERY);
    public static final ResourceLocation MECHANICAL_DAISY_GUI = gui(LibNames.MECHANICAL_DAISY);
    public static final ResourceLocation MECHANICAL_MANA_POOL_GUI = gui(LibNames.MECHANICAL_MANA_POOL);
    public static final ResourceLocation MECHANICAL_RUNIC_ALTAR_GUI = gui(LibNames.MECHANICAL_RUNIC_ALTAR);

    private static ResourceLocation gui(String id) {
        return loc(GUI_PREFIX + id);
    }

    private static ResourceLocation loc(String id) {
        return new ResourceLocation(BotanicalMachinery.MODID, id + ".png");
    }

}
