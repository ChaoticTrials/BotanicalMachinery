package de.melanx.botanicalmachinery.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ServerConfig {

    public static final ForgeConfigSpec SERVER_CONFIG;
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(SERVER_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static ForgeConfigSpec.IntValue multiplierAlfheimMarket;
    public static ForgeConfigSpec.IntValue multiplierAgglomerationFactory;
    public static ForgeConfigSpec.IntValue multiplierManaPool;
    public static ForgeConfigSpec.IntValue multiplierRunicAltar;
    public static ForgeConfigSpec.IntValue multiplierDaisy;
    public static ForgeConfigSpec.IntValue multiplierBrewery;
    public static ForgeConfigSpec.IntValue multiplierApothecary;

    public static ForgeConfigSpec.IntValue alfheimMarketRecipeCost;

    public static ForgeConfigSpec.IntValue capacityAlfheimMarket;
    public static ForgeConfigSpec.IntValue capacityAgglomerationFactory;
    public static ForgeConfigSpec.IntValue capacityManaPool;
    public static ForgeConfigSpec.IntValue capacityRunicAltar;
    public static ForgeConfigSpec.IntValue capacityBrewery;
    public static ForgeConfigSpec.IntValue capacityManaBattery;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("working-duration-multiplier");
        builder.comment("The default duration multiplied with this will be the used working duration.");
        multiplierAlfheimMarket = builder.defineInRange("alfheim-market", 1, 1, TileAlfheimMarket.MAX_MANA_PER_TICK);
        multiplierAgglomerationFactory = builder.defineInRange("industrial-agglomeration-factory", 1, 1, TileIndustrialAgglomerationFactory.MAX_MANA_PER_TICK);
        multiplierManaPool = builder.defineInRange("mechanical-mana-pool", 1, 1, Integer.MAX_VALUE);
        multiplierRunicAltar = builder.defineInRange("mechanical-runic-altar", 1, 1, TileMechanicalRunicAltar.MAX_MANA_PER_TICK);
        multiplierDaisy = builder.defineInRange("mechanical-daisy", 3, 1, Integer.MAX_VALUE);
        multiplierBrewery = builder.defineInRange("mechanical-brewery", 1, 1, TileMechanicalBrewery.MAX_MANA_PER_TICK);
        multiplierApothecary = builder.defineInRange("mechanical-apothecary", 1, 1, Integer.MAX_VALUE);
        builder.pop();

        alfheimMarketRecipeCost = builder.comment("The amount of mana used in alfheim market to trade items [Default: 500]")
                .defineInRange("alfheim-market.recipe-cost", 500, 1, Integer.MAX_VALUE);

        builder.push("max-mana-capacity");
        builder.comment("The default amount of mana capacity in each machine.");
        capacityAlfheimMarket = builder.defineInRange("alfheim-market", 100_000, 1, Integer.MAX_VALUE);
        capacityAgglomerationFactory = builder.defineInRange("industrial-agglomeration-factory", 1_000_000, 500_000, Integer.MAX_VALUE);
        capacityManaPool = builder.defineInRange("mechanical-mana-pool", 100_000, 1, Integer.MAX_VALUE);
        capacityRunicAltar = builder.defineInRange("mechanical-runic-altar", 250_000, 1, Integer.MAX_VALUE);
        capacityBrewery = builder.defineInRange("mechanical-brewery", 100_000, 1, Integer.MAX_VALUE);
        capacityManaBattery = builder.defineInRange("mana-battery", 10_000_000, 1, Integer.MAX_VALUE);
        builder.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        BotanicalMachinery.getInstance().logger.debug("Loading config file {}", path);
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }
}
