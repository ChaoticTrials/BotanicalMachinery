package de.melanx.botanicalmachinery.compat;

import net.minecraftforge.fml.ModList;

public class MythicBotanyCompat {

    public static final String MOD_ID = "mythicbotany";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
}
