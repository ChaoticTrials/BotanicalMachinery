package de.melanx.botanicalmachinery.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ManaBatteryLockedSerializer implements PacketSerializer<ManaBatteryLockedSerializer.Message> {

    @Override
    public Class<Message> messageClass() {
        return Message.class;
    }

    @Override
    public void encode(Message msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.pos);
        buffer.writeBoolean(msg.locked1);
        buffer.writeBoolean(msg.locked2);
    }

    @Override
    public Message decode(FriendlyByteBuf buffer) {
        return new Message(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBoolean());
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
