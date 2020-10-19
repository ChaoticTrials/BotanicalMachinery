package de.melanx.botanicalmachinery.core.registration;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import de.melanx.botanicalmachinery.core.LibNames;
import io.github.noeppi_noeppi.libx.mod.registration.BlockBase;
import io.github.noeppi_noeppi.libx.mod.registration.BlockTE;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class ModBlocks {
    public static final BlockBase MANA_EMERALD = new BlockBase(BotanicalMachinery.getInstance(), Block.Properties.create(Material.IRON, MaterialColor.EMERALD).harvestLevel(3).harvestTool(ToolType.PICKAXE).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL));
    public static final BlockTE<TileAlfheimMarket> ALFHEIM_MARKET = new BlockAlfheimMarket(TileAlfheimMarket.class);
    public static final BlockTE<TileIndustrialAgglomerationFactory> INDUSTRIAL_AGGLOMERATION_FACTORY = new BlockIndustrialAgglomerationFactory(TileIndustrialAgglomerationFactory.class);
    public static final BlockTE<TileManaBattery> MANA_BATTERY = new BlockManaBattery(TileManaBattery.class, BlockManaBattery.Variant.NORMAL);
    public static final BlockTE<TileManaBattery> CREATIVE_MANA_BATTERY = new BlockManaBattery(TileManaBattery.class, BlockManaBattery.Variant.CREATIVE);
    public static final BlockTE<TileMechanicalApothecary> MECHANICAL_APOTHECARY = new BlockMechanicalApothecary(TileMechanicalApothecary.class);
    public static final BlockTE<TileMechanicalBrewery> MECHANICAL_BREWERY = new BlockMechanicalBrewery(TileMechanicalBrewery.class);
    public static final BlockTE<TileMechanicalDaisy> MECHANICAL_DAISY = new BlockMechanicalDaisy(TileMechanicalDaisy.class);
    public static final BlockTE<TileMechanicalManaPool> MECHANICAL_MANA_POOL = new BlockMechanicalManaPool(TileMechanicalManaPool.class);
    public static final BlockTE<TileMechanicalRunicAltar> MECHANICAL_RUNIC_ALTAR = new BlockMechanicalRunicAltar(TileMechanicalRunicAltar.class);

    public static void register() {
        BotanicalMachinery.getInstance().register(LibNames.MANA_EMERALD_BLOCK, MANA_EMERALD);
        BotanicalMachinery.getInstance().register(LibNames.ALFHEIM_MARKET, ALFHEIM_MARKET);
        BotanicalMachinery.getInstance().register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, INDUSTRIAL_AGGLOMERATION_FACTORY);
        BotanicalMachinery.getInstance().register(LibNames.MANA_BATTERY, MANA_BATTERY);
        BotanicalMachinery.getInstance().register(LibNames.MANA_BATTERY_CREATIVE, CREATIVE_MANA_BATTERY);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_APOTHECARY, MECHANICAL_APOTHECARY);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_BREWERY, MECHANICAL_BREWERY);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_DAISY, MECHANICAL_DAISY);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_MANA_POOL, MECHANICAL_MANA_POOL);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_RUNIC_ALTAR, MECHANICAL_RUNIC_ALTAR);
    }
}
