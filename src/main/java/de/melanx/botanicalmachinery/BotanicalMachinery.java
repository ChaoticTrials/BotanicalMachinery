package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.blocks.tesr.*;
import de.melanx.botanicalmachinery.data.DataCreator;
import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;

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
        try {
            Files.deleteIfExists(FMLPaths.CONFIGDIR.get().resolve(this.modid + "-client.toml"));
        } catch (IOException e) {
            this.logger.error("config/botanicalmachinery-client.toml could not be deleted");
        }

        try {
            Files.deleteIfExists(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(this.modid + "-server.toml"));
        } catch (IOException e) {
            this.logger.error("defaultconfigs/botanicalmachinery-server.toml could not be deleted");
        }
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
}
