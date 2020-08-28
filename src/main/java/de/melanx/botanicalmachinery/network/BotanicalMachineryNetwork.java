package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class BotanicalMachineryNetwork {

    private BotanicalMachineryNetwork() {

    }

    private static final String PROTOCOL_VERSION = "1";
    private static int discriminator = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BotanicalMachinery.MODID, "netchannel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        register(new ManaBatteryLockedHandler(), NetworkDirection.PLAY_TO_SERVER);
    }

    private static <T> void register(PacketHandler<T> handler, NetworkDirection direction) {
        INSTANCE.registerMessage(discriminator++, handler.messageClass(), handler::encode, handler::decode, handler::handle, Optional.of(direction));
    }

    public static void updateLockedState(TileManaBattery tile) {
        if (tile.getWorld() != null && tile.getWorld().isRemote) {
            INSTANCE.sendToServer(new ManaBatteryLockedHandler.Message(tile.getPos(), tile.isSlot1Locked(), tile.isSlot2Locked()));
        }
    }
}
