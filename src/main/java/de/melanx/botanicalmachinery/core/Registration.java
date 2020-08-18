package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.*;
import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.containers.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class Registration {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BotanicalMachinery.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BotanicalMachinery.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BotanicalMachinery.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BotanicalMachinery.MODID);
    private static final Item.Properties itemProps = new Item.Properties().group(BotanicalMachinery.itemGroup);

    public static final RegistryObject<Block> BLOCK_ALFHEIM_MARKET = BLOCKS.register(LibNames.ALFHEIM_MARKET, BlockAlfheimMarket::new);
    public static final RegistryObject<Block> BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY = BLOCKS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, BlockIndustrialAgglomerationFactory::new);
    public static final RegistryObject<Block> BLOCK_MANA_BATTERY = BLOCKS.register(LibNames.MANA_BATTERY, BlockManaBattery::new);
    public static final RegistryObject<Block> BLOCK_MECHANICAL_BREWERY = BLOCKS.register(LibNames.MECHANICAL_BREWERY, BlockMechanicalBrewery::new);
    public static final RegistryObject<Block> BLOCK_MECHANICAL_MANA_POOL = BLOCKS.register(LibNames.MECHANICAL_MANA_POOL, BlockMechanicalManaPool::new);
    public static final RegistryObject<Block> BLOCK_MECHANICAL_RUNIC_ALTAR = BLOCKS.register(LibNames.MECHANICAL_RUNIC_ALTAR, BlockMechanicalRunicAltar::new);
    public static final RegistryObject<Block> BLOCK_MECHANICAL_DAISY = BLOCKS.register(LibNames.MECHANICAL_DAISY, BlockMechanicalDaisy::new);

    public static final RegistryObject<Item> ITEM_ALFHEIM_MARKET = ITEMS.register(LibNames.ALFHEIM_MARKET, () -> new BlockItem(BLOCK_ALFHEIM_MARKET.get(), itemProps));
    public static final RegistryObject<Item> ITEM_INDUSTRIAL_AGGLOMERATION_FACTORY = ITEMS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> new BlockItem(BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), itemProps));
    public static final RegistryObject<Item> ITEM_MANA_BATTERY = ITEMS.register(LibNames.MANA_BATTERY, () -> new BlockItem(BLOCK_MANA_BATTERY.get(), itemProps));
    public static final RegistryObject<Item> ITEM_MECHANICAL_BREWERY = ITEMS.register(LibNames.MECHANICAL_BREWERY, () -> new BlockItem(BLOCK_MECHANICAL_BREWERY.get(), itemProps));
    public static final RegistryObject<Item> ITEM_MECHANICAL_MANA_POOL = ITEMS.register(LibNames.MECHANICAL_MANA_POOL, () -> new BlockItem(BLOCK_MECHANICAL_MANA_POOL.get(), itemProps));
    public static final RegistryObject<Item> ITEM_MECHANICAL_RUNIC_ALTAR = ITEMS.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> new BlockItem(BLOCK_MECHANICAL_RUNIC_ALTAR.get(), itemProps));
    public static final RegistryObject<Item> ITEM_MECHANICAL_DAISY = ITEMS.register(LibNames.MECHANICAL_DAISY, () -> new BlockItem(BLOCK_MECHANICAL_DAISY.get(), itemProps));

    public static final RegistryObject<TileEntityType<TileAlfheimMarket>> TILE_ALFHEIM_MARKET = TILES.register(LibNames.ALFHEIM_MARKET, () -> TileEntityType.Builder.create(TileAlfheimMarket::new, BLOCK_ALFHEIM_MARKET.get()).build(null));
    public static final RegistryObject<TileEntityType<TileIndustrialAgglomerationFactory>> TILE_INDUSTRIAL_AGGLOMERATION_FACTORY = TILES.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> TileEntityType.Builder.create(TileIndustrialAgglomerationFactory::new, BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get()).build(null));
    public static final RegistryObject<TileEntityType<TileManaBattery>> TILE_MANA_BATTERY = TILES.register(LibNames.MANA_BATTERY, () -> TileEntityType.Builder.create(TileManaBattery::new, BLOCK_MANA_BATTERY.get()).build(null));
    public static final RegistryObject<TileEntityType<TileMechanicalBrewery>> TILE_MECHANICAL_BREWERY = TILES.register(LibNames.MECHANICAL_BREWERY, () -> TileEntityType.Builder.create(TileMechanicalBrewery::new, BLOCK_MECHANICAL_BREWERY.get()).build(null));
    public static final RegistryObject<TileEntityType<TileMechanicalManaPool>> TILE_MECHANICAL_MANA_POOL = TILES.register(LibNames.MECHANICAL_MANA_POOL, () -> TileEntityType.Builder.create(TileMechanicalManaPool::new, BLOCK_MECHANICAL_MANA_POOL.get()).build(null));
    public static final RegistryObject<TileEntityType<TileMechanicalRunicAltar>> TILE_MECHANICAL_RUNIC_ALTAR = TILES.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> TileEntityType.Builder.create(TileMechanicalRunicAltar::new, BLOCK_MECHANICAL_RUNIC_ALTAR.get()).build(null));
    public static final RegistryObject<TileEntityType<TileMechanicalDaisy>> TILE_MECHANICAL_DAISY = TILES.register(LibNames.MECHANICAL_DAISY, () -> TileEntityType.Builder.create(TileMechanicalDaisy::new, BLOCK_MECHANICAL_DAISY.get()).build(null));

    public static final RegistryObject<ContainerType<ContainerAlfheimMarket>> CONTAINER_ALFHEIM_MARKET = CONTAINERS.register(LibNames.ALFHEIM_MARKET, () -> ContainerBase.createContainerType(ContainerAlfheimMarket::new));
    public static final RegistryObject<ContainerType<ContainerIndustrialAgglomerationFactory>> CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY = CONTAINERS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> ContainerBase.createContainerType(ContainerIndustrialAgglomerationFactory::new));
    public static final RegistryObject<ContainerType<ContainerManaBattery>> CONTAINER_MANA_BATTERY = CONTAINERS.register(LibNames.MANA_BATTERY, () -> ContainerBase.createContainerType(ContainerManaBattery::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalBrewery>> CONTAINER_MECHANICAL_BREWERY = CONTAINERS.register(LibNames.MECHANICAL_BREWERY, () -> ContainerBase.createContainerType(ContainerMechanicalBrewery::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalManaPool>> CONTAINER_MECHANICAL_MANA_POOL = CONTAINERS.register(LibNames.MECHANICAL_MANA_POOL, () -> ContainerBase.createContainerType(ContainerMechanicalManaPool::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalRunicAltar>> CONTAINER_MECHANICAL_RUNIC_ALTAR = CONTAINERS.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> ContainerBase.createContainerType(ContainerMechanicalRunicAltar::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalDaisy>> CONTAINER_MECHANICAL_DAISY = CONTAINERS.register(LibNames.MECHANICAL_DAISY, () -> ContainerBase.createContainerType(ContainerMechanicalDaisy::new));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BotanicalMachinery.LOGGER.info(ITEMS.getEntries().size() + " items registered.");
        BLOCKS.register(bus);
        BotanicalMachinery.LOGGER.info(BLOCKS.getEntries().size() + " blocks registered.");
        TILES.register(bus);
        BotanicalMachinery.LOGGER.info(TILES.getEntries().size() + " tiles registered.");
        CONTAINERS.register(bus);
        BotanicalMachinery.LOGGER.info(CONTAINERS.getEntries().size() + " containers registered.");
    }
}
