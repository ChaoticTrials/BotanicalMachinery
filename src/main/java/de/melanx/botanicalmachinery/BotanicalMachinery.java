package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.ScreenIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalRunicAltar;
import de.melanx.botanicalmachinery.core.ModGroup;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
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
    public static final ItemGroup itemGroup = new ModGroup(MODID);
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public BotanicalMachinery instance;

    public BotanicalMachinery() {
        instance = this;
        Registration.init();
        MinecraftForge.EVENT_BUS.register(RecipeHelper.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_MANA_POOL.get(), ScreenMechanicalManaPool::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_RUNIC_ALTAR.get(), ScreenMechanicalRunicAltar::new);
        ScreenManager.registerFactory(Registration.CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), ScreenIndustrialAgglomerationFactory::new);
    }
}
