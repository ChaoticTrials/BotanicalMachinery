package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.*;
import de.melanx.botanicalmachinery.blocks.containers.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.base.BlockBase;
import org.moddingx.libx.base.tile.MenuBlockBE;
import org.moddingx.libx.menu.BlockEntityMenu;

@RegisterClass(registry = "BLOCK_REGISTRY")
public class ModBlocks {

    public static final BlockBase manaEmeraldBlock = new BlockBase(BotanicalMachinery.getInstance(), Block.Properties.of(Material.METAL, MaterialColor.EMERALD).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final MenuBlockBE<BlockEntityAlfheimMarket, ContainerMenuAlfheimMarket> alfheimMarket = new BlockAlfheimMarket(BotanicalMachinery.getInstance(), BlockEntityAlfheimMarket.class, BlockEntityMenu.createMenuType(ContainerMenuAlfheimMarket::new));
    public static final MenuBlockBE<BlockEntityIndustrialAgglomerationFactory, ContainerMenuIndustrialAgglomerationFactory> industrialAgglomerationFactory = new BlockIndustrialAgglomerationFactory(BotanicalMachinery.getInstance(), BlockEntityIndustrialAgglomerationFactory.class, BlockEntityMenu.createMenuType(ContainerMenuIndustrialAgglomerationFactory::new));
    public static final MenuBlockBE<BlockEntityManaBattery, ContainerMenuManaBattery> manaBattery = new BlockManaBattery(BotanicalMachinery.getInstance(), BlockEntityManaBattery.class, BlockEntityMenu.createMenuType(ContainerMenuManaBattery::new), BlockManaBattery.Variant.NORMAL);
    public static final MenuBlockBE<BlockEntityManaBattery, ContainerMenuManaBattery> manaBatteryCreative = new BlockManaBattery(BotanicalMachinery.getInstance(), BlockEntityManaBattery.class, BlockEntityMenu.createMenuType(ContainerMenuManaBattery::new), BlockManaBattery.Variant.CREATIVE);
    public static final MenuBlockBE<BlockEntityMechanicalApothecary, ContainerMenuMechanicalApothecary> mechanicalApothecary = new BlockMechanicalApothecary(BotanicalMachinery.getInstance(), BlockEntityMechanicalApothecary.class, BlockEntityMenu.createMenuType(ContainerMenuMechanicalApothecary::new));
    public static final MenuBlockBE<BlockEntityMechanicalBrewery, ContainerMenuMechanicalBrewery> mechanicalBrewery = new BlockMechanicalBrewery(BotanicalMachinery.getInstance(), BlockEntityMechanicalBrewery.class, BlockEntityMenu.createMenuType(ContainerMenuMechanicalBrewery::new));
    public static final MenuBlockBE<BlockEntityMechanicalDaisy, ContainerMenuMechanicalDaisy> mechanicalDaisy = new BlockMechanicalDaisy(BotanicalMachinery.getInstance(), BlockEntityMechanicalDaisy.class, BlockEntityMenu.createMenuType(ContainerMenuMechanicalDaisy::new));
    public static final MenuBlockBE<BlockEntityMechanicalManaPool, ContainerMenuMechanicalManaPool> mechanicalManaPool = new BlockMechanicalManaPool(BotanicalMachinery.getInstance(), BlockEntityMechanicalManaPool.class, BlockEntityMenu.createMenuType(ContainerMenuMechanicalManaPool::new));
    public static final MenuBlockBE<BlockEntityMechanicalRunicAltar, ContainerMenuMechanicalRunicAltar> mechanicalRunicAltar = new BlockMechanicalRunicAltar(BotanicalMachinery.getInstance(), BlockEntityMechanicalRunicAltar.class, BlockEntityMenu.createMenuType(ContainerMenuMechanicalRunicAltar::new));
}
