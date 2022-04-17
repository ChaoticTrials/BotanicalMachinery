package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraftforge.network.NetworkDirection;

public class BotanicalMachineryNetwork extends NetworkX {

    public BotanicalMachineryNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("3");
    }

    @Override
    protected void registerPackets() {
        this.register(new ManaBatteryLockedSerializer(), () -> ManaBatteryLockedHandler::handle, NetworkDirection.PLAY_TO_SERVER);
    }

    public void updateLockedState(BlockEntityManaBattery tile) {
        if (tile.getLevel() != null && tile.getLevel().isClientSide) {
            this.channel.sendToServer(new ManaBatteryLockedSerializer.Message(tile.getBlockPos(), tile.isSlot1Locked(), tile.isSlot2Locked()));
        }
    }
}
