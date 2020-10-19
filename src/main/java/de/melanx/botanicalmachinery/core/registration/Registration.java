package de.melanx.botanicalmachinery.core.registration;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.containers.*;
import de.melanx.botanicalmachinery.core.LibNames;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BotanicalMachinery.getInstance().modid);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BotanicalMachinery.getInstance().modid);

    public static final RegistryObject<ContainerType<ContainerAlfheimMarket>> CONTAINER_ALFHEIM_MARKET = CONTAINERS.register(LibNames.ALFHEIM_MARKET, () -> ContainerBase.createContainerType(ContainerAlfheimMarket::new));
    public static final RegistryObject<ContainerType<ContainerIndustrialAgglomerationFactory>> CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY = CONTAINERS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> ContainerBase.createContainerType(ContainerIndustrialAgglomerationFactory::new));
    public static final RegistryObject<ContainerType<ContainerManaBattery>> CONTAINER_MANA_BATTERY = CONTAINERS.register(LibNames.MANA_BATTERY, () -> ContainerBase.createContainerType(ContainerManaBattery::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalApothecary>> CONTAINER_MECHANICAL_APOTHECARY = CONTAINERS.register(LibNames.MECHANICAL_APOTHECARY, () -> ContainerBase.createContainerType(ContainerMechanicalApothecary::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalBrewery>> CONTAINER_MECHANICAL_BREWERY = CONTAINERS.register(LibNames.MECHANICAL_BREWERY, () -> ContainerBase.createContainerType(ContainerMechanicalBrewery::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalDaisy>> CONTAINER_MECHANICAL_DAISY = CONTAINERS.register(LibNames.MECHANICAL_DAISY, () -> ContainerBase.createContainerType(ContainerMechanicalDaisy::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalManaPool>> CONTAINER_MECHANICAL_MANA_POOL = CONTAINERS.register(LibNames.MECHANICAL_MANA_POOL, () -> ContainerBase.createContainerType(ContainerMechanicalManaPool::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalRunicAltar>> CONTAINER_MECHANICAL_RUNIC_ALTAR = CONTAINERS.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> ContainerBase.createContainerType(ContainerMechanicalRunicAltar::new));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CONTAINERS.register(bus);
        BotanicalMachinery.getInstance().logger.info(CONTAINERS.getEntries().size() + " containers registered.");
    }
}
