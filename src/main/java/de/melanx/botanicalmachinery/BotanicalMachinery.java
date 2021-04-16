package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.blocks.tesr.*;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.LibXClientConfig;
import de.melanx.botanicalmachinery.config.LibXServerConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.data.DataCreator;
import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import io.github.noeppi_noeppi.libx.config.ConfigManager;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

        // TODO 1.17 remove this as fast as possible
        LibXClientConfig.numericalMana = ClientConfig.numericalMana.get();
        LibXClientConfig.AdvancedRendering.all = ClientConfig.everything.get();
        LibXClientConfig.AdvancedRendering.industrialAgglomerationFactory = ClientConfig.agglomerationFactory.get();
        LibXClientConfig.AdvancedRendering.mechanicalDaisy = ClientConfig.daisy.get();
        LibXClientConfig.AdvancedRendering.mechanicalBrewery = ClientConfig.brewery.get();
        LibXClientConfig.AdvancedRendering.mechanicalManaPool = ClientConfig.manaPool.get();
        LibXClientConfig.AdvancedRendering.alfheimMarket = ClientConfig.alfheimMarket.get();
        LibXClientConfig.AdvancedRendering.mechanicalApothecary = ClientConfig.apothecary.get();
        LibXClientConfig.AdvancedRendering.mechanicalRunicAltar = ClientConfig.runicAltar.get();

        LibXServerConfig.AlfheimMarket.recipeCost = ServerConfig.alfheimMarketRecipeCost.get();
        LibXServerConfig.MaxManaCapacity.industrialAgglomerationFactory = ServerConfig.capacityAgglomerationFactory.get();
        LibXServerConfig.MaxManaCapacity.mechanicalBrewery = ServerConfig.capacityBrewery.get();
        LibXServerConfig.MaxManaCapacity.manaBattery = ServerConfig.capacityManaBattery.get();
        LibXServerConfig.MaxManaCapacity.mechanicalManaPool = ServerConfig.capacityManaPool.get();
        LibXServerConfig.MaxManaCapacity.alfheimMarket = ServerConfig.capacityAlfheimMarket.get();
        LibXServerConfig.MaxManaCapacity.mechanicalRunicAltar = ServerConfig.capacityRunicAltar.get();
        LibXServerConfig.WorkingDurationMultiplier.industrialAgglomerationFactory = ServerConfig.multiplierAgglomerationFactory.get();
        LibXServerConfig.WorkingDurationMultiplier.mechanicalDaisy = ServerConfig.multiplierDaisy.get();
        LibXServerConfig.WorkingDurationMultiplier.mechanicalBrewery = ServerConfig.multiplierBrewery.get();
        LibXServerConfig.WorkingDurationMultiplier.mechanicalManaPool = ServerConfig.multiplierManaPool.get();
        LibXServerConfig.WorkingDurationMultiplier.alfheimMarket = ServerConfig.multiplierAlfheimMarket.get();
        LibXServerConfig.WorkingDurationMultiplier.mechanicalApothecary = ServerConfig.multiplierApothecary.get();
        LibXServerConfig.WorkingDurationMultiplier.mechanicalRunicAltar = ServerConfig.multiplierRunicAltar.get();

        ConfigManager.registerConfig(new ResourceLocation("botanicalmachinery", "client"), LibXClientConfig.class, true);
        ConfigManager.registerConfig(new ResourceLocation("botanicalmachinery", "server"), LibXServerConfig.class, false);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataCreator::onGatherData);
        MinecraftForge.EVENT_BUS.addListener(this::onJoinWorld);
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
        ScreenManager.registerFactory(ModBlocks.industrialAgglomerationFactory.container, ScreenIndustrialAgglomerationFactory::new);
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
        ClientRegistry.bindTileEntityRenderer(ModBlocks.industrialAgglomerationFactory.getTileType(), TesrIndustrialAgglomerationFactory::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalApothecary.getTileType(), TesrMechanicalApothecary::new);
        ClientRegistry.bindTileEntityRenderer(ModBlocks.mechanicalBrewery.getTileType(), TesrMechanicalBrewery::new);
    }

    private void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (LibXServerConfig.reminder && !event.getPlayer().world.isRemote) {
            event.getPlayer().sendStatusMessage(new StringTextComponent("[IMPORTANT] The configs changed to LibX configs. Please make sure to change config from now in configs/botanicalmachinery/ and ignore/delete the old ones! " +
                    "Your current configs were converted to LibX configs. You can disable this message in config.").mergeStyle(TextFormatting.RED), false);
        }
    }
}
