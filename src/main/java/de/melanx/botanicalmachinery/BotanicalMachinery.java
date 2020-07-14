package de.melanx.botanicalmachinery;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BotanicalMachinery.MODID)
public class BotanicalMachinery {

    public static final String MODID = "botanicalmachinery";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public BotanicalMachinery instance;

    public BotanicalMachinery() {
        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }
}
