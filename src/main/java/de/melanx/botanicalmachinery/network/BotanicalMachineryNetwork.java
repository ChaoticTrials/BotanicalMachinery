package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class BotanicalMachineryNetwork extends NetworkX {

    public BotanicalMachineryNetwork(ModX mod) {
        super(mod);
    }

    private static final String PROTOCOL_VERSION = "1";
    private static int discriminator = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BotanicalMachinery.getInstance().modid, "netchannel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @Override
    protected String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    @Override
    protected void registerPackets() {
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
