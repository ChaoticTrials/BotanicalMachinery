package de.melanx.modid;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ModName.MODID)
public class ModName {

    public static final String MODID = "modid";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public ModName instance;

    public ModName() {
        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }
}
