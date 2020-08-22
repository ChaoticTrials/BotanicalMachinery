package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.blocks.tesr.TesrAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tesr.TesrMechanicalDaisy;
import de.melanx.botanicalmachinery.core.ModGroup;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
    public final BotanicalMachinery instance;

    public BotanicalMachinery() {
        this.instance = this;
        Registration.init();
        MinecraftForge.EVENT_BUS.register(RecipeHelper.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.CONTAINER_ALFHEIM_MARKET.get(), ScreenAlfheimMarket::new);
        ScreenManager.registerFactory(Registration.CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), ScreenIndustrialAgglomerationFactory::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MANA_BATTERY.get(), ScreenManaBattery::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_APOTHECARY.get(), ScreenMechanicalApothecary::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_BREWERY.get(), ScreenMechanicalBrewery::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_DAISY.get(), ScreenMechanicalDaisy::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_MANA_POOL.get(), ScreenMechanicalManaPool::new);
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_RUNIC_ALTAR.get(), ScreenMechanicalRunicAltar::new);

        RenderTypeLookup.setRenderLayer(Registration.BLOCK_MECHANICAL_DAISY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(Registration.BLOCK_ALFHEIM_MARKET.get(), RenderType.getCutout());

        ClientRegistry.bindTileEntityRenderer(Registration.TILE_MECHANICAL_DAISY.get(), TesrMechanicalDaisy::new);
        ClientRegistry.bindTileEntityRenderer(Registration.TILE_ALFHEIM_MARKET.get(), TesrAlfheimMarket::new);
    }
}
