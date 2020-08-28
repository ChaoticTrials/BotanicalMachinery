package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import java.util.function.Supplier;

public class ManaBatteryLockedHandler implements PacketHandler<ManaBatteryLockedHandler.Message> {

    @Override
    public Class<Message> messageClass() {
        return Message.class;
    }

    @Override
    public void encode(Message msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.pos);
        buffer.writeBoolean(msg.locked1);
        buffer.writeBoolean(msg.locked2);
    }

    @Override
    public Message decode(PacketBuffer buffer) {
        return new Message(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void handle(Message msg, Supplier<NetworkEvent.Context> ctx) {
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
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
                }
            }
        });
    }

    public static class Message {

        public Message(BlockPos pos, boolean locked1, boolean locked2) {
            this.pos = pos;
            this.locked1 = locked1;
            this.locked2 = locked2;
        }

        public BlockPos pos;
        public boolean locked1;
        public boolean locked2;
    }
}
