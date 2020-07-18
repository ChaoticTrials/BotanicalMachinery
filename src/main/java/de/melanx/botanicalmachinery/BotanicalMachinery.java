package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.ScreenIndustrialAgglomarationFactory;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaPool;
import de.melanx.botanicalmachinery.core.ModGroup;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.common.block.tile.mana.TilePool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(BotanicalMachinery.MODID)
public class BotanicalMachinery {

    public static final String MODID = "botanicalmachinery";
    public static final ItemGroup itemGroup = new ModGroup(MODID);
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public static List<Item> catalysts = new ArrayList<>();
    public BotanicalMachinery instance;

    public BotanicalMachinery() {
        instance = this;
        Registration.init();
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.CONTAINER_MECHANICAL_MANA_POOL.get(), ScreenMechanicalManaPool::new);
        ScreenManager.registerFactory(Registration.CONTAINER_INDUSTRIAL_AGGLOMARATION_FACTORY.get(), ScreenIndustrialAgglomarationFactory::new);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class EventHandler {
        @SubscribeEvent
        public static void onRecipesUpdated(final RecipesUpdatedEvent event) {
            for (IManaInfusionRecipe recipe : TilePool.manaInfusionRecipes(event.getRecipeManager())) {
                if (recipe.getCatalyst() != null) {
                    Item catalyst = recipe.getCatalyst().getBlock().asItem();
                    if (!catalysts.contains(catalyst))
                        catalysts.add(catalyst);
                }
            }
            LOGGER.info("All catalysts: " + Arrays.toString(catalysts.toArray()));
        }
    }
}
