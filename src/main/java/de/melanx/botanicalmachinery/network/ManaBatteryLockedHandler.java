package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import io.github.noeppi_noeppi.libx.LibX;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import java.util.function.Supplier;

public class ManaBatteryLockedHandler {

    public static void handle(ManaBatteryLockedSerializer.Message msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null)
                return;
            ServerWorld world = player.getServerWorld();
            if (world.isBlockLoaded(msg.pos)) {
                TileEntity te = world.getTileEntity(msg.pos);
                if (te instanceof TileManaBattery) {
                    ((TileManaBattery) te).setSlot1Locked(msg.locked1);
                    ((TileManaBattery) te).setSlot2Locked(msg.locked2);
                    LibX.getNetwork().updateTE(world, msg.pos);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
