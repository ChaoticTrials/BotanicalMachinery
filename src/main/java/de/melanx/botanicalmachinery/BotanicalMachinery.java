package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.ScreenManaBlock;
import de.melanx.botanicalmachinery.core.ModGroup;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BotanicalMachinery.MODID)
public class BotanicalMachinery {

    public static final String MODID = "botanicalmachinery";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final ItemGroup itemGroup = new ModGroup(MODID);
    public BotanicalMachinery instance;

    public BotanicalMachinery() {
        instance = this;
        Registration.init();
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.CONTAINER_MANA_BLOCK.get(), ScreenManaBlock::new);
    }
}
