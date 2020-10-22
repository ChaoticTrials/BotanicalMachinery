package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.blocks.tesr.*;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.data.DataCreator;
import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nonnull;

@Mod("botanicalmachinery")
public class BotanicalMachinery extends ModXRegistration {

    public static BotanicalMachinery instance;
    public static BotanicalMachineryNetwork network;

    public BotanicalMachinery() {
        super("botanicalmachinery", new ItemGroup("botanicalmachinery") {
            @Nonnull
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ModBlocks.mechanicalManaPool);
            }
        });

        instance = this;
        network = new BotanicalMachineryNetwork(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
        ClientConfig.loadConfig(ClientConfig.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(this.modid + "-client.toml"));
        ServerConfig.loadConfig(ServerConfig.SERVER_CONFIG, FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(this.modid + "-server.toml"));

        this.addRegistrationHandler(ModBlocks::register);
        this.addRegistrationHandler(ModItems::register);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataCreator::onGatherData);
    }

    public static BotanicalMachinery getInstance() {
        return instance;
    }

    public static BotanicalMachineryNetwork getNetwork() {
        return network;
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {

    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModBlocks.alfheimMarket.container, ScreenAlfheimMarket::new);
        ScreenManager.registerFactory(ModBlocks.industrialAgglommerationFactory.container, ScreenIndustrialAgglomerationFactory::new);
        ScreenManager.registerFactory(ModBlocks.manaBattery.container, ScreenManaBattery::new);
        ScreenManager.registerFactory(ModBlocks.manaBatteryCreative.container, ScreenManaBattery::new);
        ScreenManager.registerFactory(ModBlocks.mechanicalApothecary.container, ScreenMechanicalApothecary::new);
        ScreenManager.registerFactory(ModBlocks.mechanicalBrewery.container, ScreenMechanicalBrewery::new);
        ScreenManager.registerFactory(ModBlocks.mechanicalDaisy.container, ScreenMechanicalDaisy::new);
        ScreenManager.registerFactory(ModBlocks.mechanicalManaPool.container, ScreenMechanicalManaPool::new);
        ScreenManager.registerFactory(ModBlocks.mechanicalRunicAltar.container, ScreenMechanicalRunicAltar::new);

        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalDaisy.getTileType(), TesrMechanicalDaisy::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.alfheimMarket.getTileType(), TesrAlfheimMarket::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalManaPool.getTileType(), TesrMechanicalManaPool::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalRunicAltar.getTileType(), TesrMechanicalRunicAltar::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.industrialAgglommerationFactory.getTileType(), TesrIndustrialAgglomerationFactory::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalApothecary.getTileType(), TesrMechanicalApothecary::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalBrewery.getTileType(), TesrMechanicalBrewery::new);
    }
}
