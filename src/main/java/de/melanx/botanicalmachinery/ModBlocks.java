package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.*;
import de.melanx.botanicalmachinery.blocks.containers.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import de.melanx.botanicalmachinery.core.LibNames;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.mod.registration.BlockBase;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import io.github.noeppi_noeppi.libx.mod.registration.BlockTE;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.ToolType;

public class ModBlocks {

    public static final BlockBase manaEmeraldBlock = new BlockBase(BotanicalMachinery.getInstance(), Block.Properties.create(Material.IRON, MaterialColor.EMERALD).harvestLevel(3).harvestTool(ToolType.PICKAXE).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL));
    public static final BlockGUI<TileAlfheimMarket, ContainerAlfheimMarket> alfheimMarket = new BlockAlfheimMarket(BotanicalMachinery.getInstance(), TileAlfheimMarket.class, ContainerBase.createContainerType(ContainerAlfheimMarket::new));
    public static final BlockGUI<TileIndustrialAgglomerationFactory, ContainerIndustrialAgglomerationFactory> industrialAgglommerationFactory = new BlockIndustrialAgglomerationFactory(BotanicalMachinery.getInstance(), TileIndustrialAgglomerationFactory.class, ContainerBase.createContainerType(ContainerIndustrialAgglomerationFactory::new));;
    public static final BlockGUI<TileManaBattery, ContainerManaBattery> manaBattery = new BlockManaBattery(BotanicalMachinery.getInstance(), TileManaBattery.class, ContainerBase.createContainerType(ContainerManaBattery::new), BlockManaBattery.Variant.NORMAL);
    public static final BlockGUI<TileManaBattery, ContainerManaBattery> manaBatteryCreative = new BlockManaBattery(BotanicalMachinery.getInstance(), TileManaBattery.class, ContainerBase.createContainerType(ContainerManaBattery::new), BlockManaBattery.Variant.CREATIVE);
    public static final BlockGUI<TileMechanicalApothecary, ContainerMechanicalApothecary> mechanicalApothecary = new BlockMechanicalApothecary(BotanicalMachinery.getInstance(), TileMechanicalApothecary.class, ContainerBase.createContainerType(ContainerMechanicalApothecary::new));
    public static final BlockGUI<TileMechanicalBrewery, ContainerMechanicalBrewery> mechanicalBrewery = new BlockMechanicalBrewery(BotanicalMachinery.getInstance(), TileMechanicalBrewery.class, ContainerBase.createContainerType(ContainerMechanicalBrewery::new));
    public static final BlockGUI<TileMechanicalDaisy, ContainerMechanicalDaisy> mechanicalDaisy = new BlockMechanicalDaisy(BotanicalMachinery.getInstance(), TileMechanicalDaisy.class, ContainerBase.createContainerType(ContainerMechanicalDaisy::new));
    public static final BlockGUI<TileMechanicalManaPool, ContainerMechanicalManaPool> mechanicalManaPool = new BlockMechanicalManaPool(BotanicalMachinery.getInstance(), TileMechanicalManaPool.class, ContainerBase.createContainerType(ContainerMechanicalManaPool::new));
    public static final BlockGUI<TileMechanicalRunicAltar, ContainerMechanicalRunicAltar> mechanicalRunicAltar = new BlockMechanicalRunicAltar(BotanicalMachinery.getInstance(), TileMechanicalRunicAltar.class, ContainerBase.createContainerType(ContainerMechanicalRunicAltar::new));

    public static void register() {
        BotanicalMachinery.getInstance().register(LibNames.MANA_EMERALD_BLOCK, manaEmeraldBlock);
        BotanicalMachinery.getInstance().register(LibNames.ALFHEIM_MARKET, alfheimMarket);
        BotanicalMachinery.getInstance().register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, industrialAgglommerationFactory);
        BotanicalMachinery.getInstance().register(LibNames.MANA_BATTERY, manaBattery);
        BotanicalMachinery.getInstance().register(LibNames.MANA_BATTERY_CREATIVE, manaBatteryCreative);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_APOTHECARY, mechanicalApothecary);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_BREWERY, mechanicalBrewery);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_DAISY, mechanicalDaisy);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_MANA_POOL, mechanicalManaPool);
        BotanicalMachinery.getInstance().register(LibNames.MECHANICAL_RUNIC_ALTAR, mechanicalRunicAltar);
    }
}
