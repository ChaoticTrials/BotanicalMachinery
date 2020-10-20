package de.melanx.botanicalmachinery.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ClientConfig {

    public static final ForgeConfigSpec CLIENT_CONFIG;
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(CLIENT_BUILDER);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static ForgeConfigSpec.BooleanValue numericalMana;

    public static ForgeConfigSpec.BooleanValue everything;
    public static ForgeConfigSpec.BooleanValue alfheimMarket;
    public static ForgeConfigSpec.BooleanValue agglomerationFactory;
    public static ForgeConfigSpec.BooleanValue apothecary;
    public static ForgeConfigSpec.BooleanValue brewery;
    public static ForgeConfigSpec.BooleanValue daisy;
    public static ForgeConfigSpec.BooleanValue manaPool;
    public static ForgeConfigSpec.BooleanValue runicAltar;

    public static void init(ForgeConfigSpec.Builder builder) {
        numericalMana = builder.comment("Should mana in GUIs be displayed with numbers?")
                .define("numericalMana", true);

        builder.push("advanced-rendering");
        builder.comment("Should the machine render its specific rendering if items are in the machine?");
        everything = builder.comment("If you turn this off, the special rendering is disabled for all machines and ignores the other config options")
                .define("all", true);
        alfheimMarket = builder.define("alfheim-market", true);
        agglomerationFactory = builder.define("industrial-agglomeration-factory", true);
        apothecary = builder.define("mechanical-apothecary", true);
        brewery = builder.define("mechanical-brewery", true);
        daisy = builder.define("mechanical-daisy", true);
        manaPool = builder.define("mechanical-mana-pool", true);
        runicAltar = builder.define("mechanical-runic-altar", true);
        builder.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        BotanicalMachinery.getInstance().logger.debug("Loading config file {}", path);
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }
}
