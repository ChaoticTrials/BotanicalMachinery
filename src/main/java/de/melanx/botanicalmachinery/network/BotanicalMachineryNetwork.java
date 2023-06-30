package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import net.minecraftforge.network.NetworkDirection;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.network.NetworkX;

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
        this.registerGame(NetworkDirection.PLAY_TO_SERVER, new ManaBatteryLockedMessage.Serializer(), () -> ManaBatteryLockedMessage.Handler::new);
    }

    public void updateLockedState(BlockEntityManaBattery tile) {
        if (tile.getLevel() != null && tile.getLevel().isClientSide) {
            this.channel.sendToServer(new ManaBatteryLockedMessage(tile.getBlockPos(), tile.isSlot1Locked(), tile.isSlot2Locked()));
        }
    }
}
