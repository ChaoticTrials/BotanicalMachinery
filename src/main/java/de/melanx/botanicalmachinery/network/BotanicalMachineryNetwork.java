package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraftforge.fml.network.NetworkDirection;

public class BotanicalMachineryNetwork extends NetworkX {

    public BotanicalMachineryNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected String getProtocolVersion() {
        return "2";
    }

    @Override
    protected void registerPackets() {
        this.register(new ManaBatteryLockedHandler(), () -> ManaBatteryLockedHandler::handle, NetworkDirection.PLAY_TO_SERVER);
    }

    public void updateLockedState(TileManaBattery tile) {
        if (tile.getWorld() != null && tile.getWorld().isRemote) {
            this.instance.sendToServer(new ManaBatteryLockedHandler.Message(tile.getPos(), tile.isSlot1Locked(), tile.isSlot2Locked()));
        }
    }
}
