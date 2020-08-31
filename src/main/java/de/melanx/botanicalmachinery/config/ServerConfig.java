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

    public static ForgeConfigSpec.IntValue alfheimMarket;
    public static ForgeConfigSpec.IntValue agglomerationFactory;
    public static ForgeConfigSpec.IntValue manaPool;
    public static ForgeConfigSpec.IntValue runicAltar;
    public static ForgeConfigSpec.IntValue daisy;
    public static ForgeConfigSpec.IntValue brewery;
    public static ForgeConfigSpec.IntValue apothecary;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("working-duration-multiplier");
        builder.comment("The default duration multiplied with this will be the used working duration.");
        alfheimMarket = builder.defineInRange("alfheim-market", 1, 1, TileAlfheimMarket.MAX_MANA_PER_TICK);
        agglomerationFactory = builder.defineInRange("industrial-agglomeration-factory", 1, 1, TileIndustrialAgglomerationFactory.MAX_MANA_PER_TICK);
        manaPool = builder.defineInRange("mechanical-mana-pool", 1, 1, Integer.MAX_VALUE);
        runicAltar = builder.defineInRange("mechanical-runic-altar", 1, 1, TileMechanicalRunicAltar.MAX_MANA_PER_TICK);
        daisy = builder.defineInRange("mechanical-daisy", 3, 1, Integer.MAX_VALUE);
        brewery = builder.defineInRange("mechanical-brewery", 1, 1, TileMechanicalBrewery.MAX_MANA_PER_TICK);
        apothecary = builder.defineInRange("mechanical-apothecary", 1, 1, Integer.MAX_VALUE);
        builder.pop();

        
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        BotanicalMachinery.LOGGER.debug("Loading config file {}", path);
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }
}
