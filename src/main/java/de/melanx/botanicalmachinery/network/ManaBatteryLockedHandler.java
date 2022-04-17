package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import io.github.noeppi_noeppi.libx.LibX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaBatteryLockedHandler {

    public static void handle(ManaBatteryLockedSerializer.Message msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;
            ServerLevel level = player.getLevel();
            //noinspection deprecation
            if (level.hasChunkAt(msg.pos)) {
                BlockEntity te = level.getBlockEntity(msg.pos);
                if (te instanceof BlockEntityManaBattery) {
                    ((BlockEntityManaBattery) te).setSlot1Locked(msg.locked1);
                    ((BlockEntityManaBattery) te).setSlot2Locked(msg.locked2);
                    LibX.getNetwork().updateBE(level, msg.pos);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
