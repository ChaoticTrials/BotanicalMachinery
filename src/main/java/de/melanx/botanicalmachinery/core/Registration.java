package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.BlockIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.BlockMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.BlockMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.containers.ContainerIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
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

public class Registration {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BotanicalMachinery.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BotanicalMachinery.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BotanicalMachinery.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BotanicalMachinery.MODID);
    private static final Item.Properties itemProps = new Item.Properties().group(BotanicalMachinery.itemGroup);

    public static final RegistryObject<Block> BLOCK_MECHANICAL_MANA_POOL = BLOCKS.register(LibNames.MECHANICAL_MANA_POOL, BlockMechanicalManaPool::new);
    public static final RegistryObject<Block> BLOCK_MECHANICAL_RUNIC_ALTAR = BLOCKS.register(LibNames.MECHANICAL_RUNIC_ALTAR, BlockMechanicalRunicAltar::new);
    public static final RegistryObject<Block> BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY = BLOCKS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, BlockIndustrialAgglomerationFactory::new);

    public static final RegistryObject<Item> ITEM_MECHANICAL_MANA_POOL = ITEMS.register(LibNames.MECHANICAL_MANA_POOL, () -> new BlockItem(BLOCK_MECHANICAL_MANA_POOL.get(), itemProps));
    public static final RegistryObject<Item> ITEM_MECHANICAL_RUNIC_ALTAR = ITEMS.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> new BlockItem(BLOCK_MECHANICAL_RUNIC_ALTAR.get(), itemProps));
    public static final RegistryObject<Item> ITEM_INDUSTRIAL_AGGLOMERATION_FACTORY = ITEMS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> new BlockItem(BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get(), itemProps));

    public static final RegistryObject<TileEntityType<?>> TILE_MECHANICAL_MANA_POOL = TILES.register(LibNames.MECHANICAL_MANA_POOL, () -> TileEntityType.Builder.create(TileMechanicalManaPool::new, Registration.BLOCK_MECHANICAL_MANA_POOL.get()).build(null));
    public static final RegistryObject<TileEntityType<?>> TILE_MECHANICAL_RUNIC_ALTAR = TILES.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> TileEntityType.Builder.create(TileMechanicalRunicAltar::new, Registration.BLOCK_MECHANICAL_RUNIC_ALTAR.get()).build(null));
    public static final RegistryObject<TileEntityType<?>> TILE_INDUSTRIAL_AGGLOMERATION_FACTORY = TILES.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> TileEntityType.Builder.create(TileIndustrialAgglomerationFactory::new, Registration.BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY.get()).build(null));

    public static final RegistryObject<ContainerType<ContainerMechanicalManaPool>> CONTAINER_MECHANICAL_MANA_POOL = CONTAINERS.register(LibNames.MECHANICAL_MANA_POOL, ContainerMechanicalManaPool::createContainerType);
    public static final RegistryObject<ContainerType<ContainerMechanicalRunicAltar>> CONTAINER_MECHANICAL_RUNIC_ALTAR = CONTAINERS.register(LibNames.MECHANICAL_RUNIC_ALTAR, ContainerMechanicalRunicAltar::createContainerType);
    public static final RegistryObject<ContainerType<ContainerIndustrialAgglomerationFactory>> CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY = CONTAINERS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, ContainerIndustrialAgglomerationFactory::createContainerType);

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
