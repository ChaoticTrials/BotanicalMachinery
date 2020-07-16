package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.BlockManaBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBlock;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBlock;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
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
    private static final Item.Properties  itemProps  = new Item.Properties().group(BotanicalMachinery.itemGroup);

    public static final RegistryObject<Block> BLOCK_MANA_BLOCK = BLOCKS.register(LibNames.MANA_BLOCK, BlockManaBlock::new);
    public static final RegistryObject<Item> ITEM_MANA_BLOCK = ITEMS.register(LibNames.MANA_BLOCK, () -> new BlockItem(BLOCK_MANA_BLOCK.get(), itemProps));
    public static final RegistryObject<TileEntityType<?>> TILE_MANA_BLOCK = TILES.register(LibNames.MANA_BLOCK, () -> TileEntityType.Builder.create(TileManaBlock::new, Registration.BLOCK_MANA_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<ContainerManaBlock>> CONTAINER_MANA_BLOCK = CONTAINERS.register(LibNames.MANA_BLOCK, () -> IForgeContainerType.create(((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.getEntityWorld();
        return new ContainerManaBlock(windowId, world, pos, inv, inv.player);
    })));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
    }

}
