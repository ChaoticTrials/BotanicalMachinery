package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.BlockIndustrialAgglomarationFactory;
import de.melanx.botanicalmachinery.blocks.BlockMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.containers.ContainerIndustrialAgglomarationFactory;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomarationFactory;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
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

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, BotanicalMachinery.MODID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, BotanicalMachinery.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, BotanicalMachinery.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, BotanicalMachinery.MODID);
    private static final Item.Properties itemProps = new Item.Properties().group(BotanicalMachinery.itemGroup);

    public static final RegistryObject<Block> BLOCK_MECHANICAL_MANA_POOL = BLOCKS.register(LibNames.MECHANICAL_MANA_POOL, BlockMechanicalManaPool::new);
    public static final RegistryObject<Block> BLOCK_INDUSTRIAL_AGGLOMARATION_FACTORY = BLOCKS.register(LibNames.INDUSTRIAL_AGGLOMARATION_FACTORY, BlockIndustrialAgglomarationFactory::new);

    public static final RegistryObject<Item> ITEM_MECHANICAL_MANA_POOL = ITEMS.register(LibNames.MECHANICAL_MANA_POOL, () -> new BlockItem(BLOCK_MECHANICAL_MANA_POOL.get(), itemProps));
    public static final RegistryObject<Item> ITEM_INDUSTRIAL_AGGLOMARATION_FACTORY = ITEMS.register(LibNames.INDUSTRIAL_AGGLOMARATION_FACTORY, () -> new BlockItem(BLOCK_INDUSTRIAL_AGGLOMARATION_FACTORY.get(), itemProps));

    public static final RegistryObject<TileEntityType<?>> TILE_MECHANICAL_MANA_POOL = TILES.register(LibNames.MECHANICAL_MANA_POOL, () -> TileEntityType.Builder.create(TileMechanicalManaPool::new, Registration.BLOCK_MECHANICAL_MANA_POOL.get()).build(null));
    public static final RegistryObject<TileEntityType<?>> TILE_INDUSTRIAL_AGGLOMARATION_FACTORY = TILES.register(LibNames.INDUSTRIAL_AGGLOMARATION_FACTORY, () -> TileEntityType.Builder.create(TileIndustrialAgglomarationFactory::new, Registration.BLOCK_INDUSTRIAL_AGGLOMARATION_FACTORY.get()).build(null));

    public static final RegistryObject<ContainerType<ContainerMechanicalManaPool>> CONTAINER_MECHANICAL_MANA_POOL = CONTAINERS.register(LibNames.MECHANICAL_MANA_POOL, ContainerMechanicalManaPool::createContainerType);
    public static final RegistryObject<ContainerType<ContainerIndustrialAgglomarationFactory>> CONTAINER_INDUSTRIAL_AGGLOMARATION_FACTORY = CONTAINERS.register(LibNames.INDUSTRIAL_AGGLOMARATION_FACTORY, ContainerIndustrialAgglomarationFactory::createContainerType);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
    }
}
