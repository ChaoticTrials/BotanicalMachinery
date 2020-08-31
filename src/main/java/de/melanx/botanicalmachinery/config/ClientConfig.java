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

    public static void init(ForgeConfigSpec.Builder builder) {
        numericalMana = builder.comment("Should mana in GUIs be displayed with numbers?")
                .define("numericalMana", true);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        BotanicalMachinery.LOGGER.debug("Loading config file {}", path);
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }
}
