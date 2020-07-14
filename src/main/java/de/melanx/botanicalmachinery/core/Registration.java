package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.BlockManaBlock;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBlock;
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

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BotanicalMachinery.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BotanicalMachinery.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BotanicalMachinery.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BotanicalMachinery.MODID);
    private static final Item.Properties  itemProps  = new Item.Properties().group(BotanicalMachinery.itemGroup);

    public static final RegistryObject<Block> BLOCK_MANA_BLOCK = BLOCKS.register("mana_block", BlockManaBlock::new);
    public static final RegistryObject<Item> ITEM_MANA_BLOCK = ITEMS.register("mana_block", () -> new BlockItem(BLOCK_MANA_BLOCK.get(), itemProps));
    public static final RegistryObject<TileEntityType<?>> TILE_MANA_BLOCK = TILES.register("mana_block", () -> TileEntityType.Builder.create(TileManaBlock::new, Registration.BLOCK_MANA_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<ContainerManaBlock>> CONTAINER_MANA_BLOCK = CONTAINERS.register("mana_block", () -> IForgeContainerType.create(((windowId, inv, data) -> {
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
