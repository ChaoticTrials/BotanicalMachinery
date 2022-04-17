package de.melanx.botanicalmachinery.config;

import io.github.noeppi_noeppi.libx.annotation.config.RegisterConfig;
import io.github.noeppi_noeppi.libx.config.Config;
import io.github.noeppi_noeppi.libx.config.Group;
import io.github.noeppi_noeppi.libx.config.validator.IntRange;

@RegisterConfig("server")
public class LibXServerConfig {

    @Group("The amount of mana used in alfheim market to trade items [Default: 500]")
    public static class AlfheimMarket {

        @Config
        @IntRange(min = 1)
        public static int recipeCost = 500;
    }

    @Group("The default amount of mana capacity in each machine.")
    public static class MaxManaCapacity {

        @Config
        @IntRange(min = 500000)
        public static int industrialAgglomerationFactory = 1000000;

        @Config
        @IntRange(min = 1)
        public static int mechanicalBrewery = 100000;

        @Config
        @IntRange(min = 1)
        public static int manaBattery = 10000000;

        @Config
        @IntRange(min = 1)
        public static int mechanicalManaPool = 100000;

        @Config
        @IntRange(min = 1)
        public static int alfheimMarket = 100000;

        @Config
        @IntRange(min = 1)
        public static int mechanicalRunicAltar = 250000;
    }

    @Group("The default duration multiplied with this will be the used working duration.")
    public static class WorkingDurationMultiplier {

        @Config
        @IntRange(min = 1, max = 5000)
        public static int industrialAgglomerationFactory = 1;

        @Config
        @IntRange(min = 1)
        public static int mechanicalDaisy = 1;

        @Config
        @IntRange(min = 1, max = 50)
        public static int mechanicalBrewery = 1;

        @Config
        @IntRange(min = 1)
        public static int mechanicalManaPool = 1;

        @Config
        @IntRange(min = 1, max = 25)
        public static int alfheimMarket = 1;

        @Config
        @IntRange(min = 1)
        public static int mechanicalApothecary = 1;

        @Config
        @IntRange(min = 1, max = 100)
        public static int mechanicalRunicAltar = 1;
    }
}
