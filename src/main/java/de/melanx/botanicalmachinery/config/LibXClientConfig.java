package de.melanx.botanicalmachinery.config;

import io.github.noeppi_noeppi.libx.annotation.RegisterConfig;
import io.github.noeppi_noeppi.libx.config.Config;
import io.github.noeppi_noeppi.libx.config.Group;

@RegisterConfig(value = "client", client = true)
public class LibXClientConfig {

    @Config("Should mana in GUIs be displayed with numbers?")
    public static boolean numericalMana = true;

    @Group("If you turn this off, the special rendering is disabled for all machines and ignores the other config options")
    public static class AdvancedRendering {
        @Config
        public static boolean all = true;

        @Config
        public static boolean industrialAgglomerationFactory = true;

        @Config
        public static boolean mechanicalDaisy = true;

        @Config
        public static boolean mechanicalBrewery = true;

        @Config
        public static boolean mechanicalManaPool = true;

        @Config
        public static boolean alfheimMarket = true;

        @Config
        public static boolean mechanicalApothecary = true;

        @Config
        public static boolean mechanicalRunicAltar = true;
    }
}
