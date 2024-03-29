package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.LibX;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record ManaBatteryLockedMessage(BlockPos pos, boolean locked1, boolean locked2) {

    public static class Handler implements PacketHandler<ManaBatteryLockedMessage> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(ManaBatteryLockedMessage msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) {
                    return;
                }

                ServerLevel level = (ServerLevel) player.level();
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
            return true;
        }
    }

    public static class Serializer implements PacketSerializer<ManaBatteryLockedMessage> {

        @Override
        public Class<ManaBatteryLockedMessage> messageClass() {
            return ManaBatteryLockedMessage.class;
        }

        @Override
        public void encode(ManaBatteryLockedMessage msg, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(msg.pos);
            buffer.writeBoolean(msg.locked1);
            buffer.writeBoolean(msg.locked2);
        }

        @Override
        public ManaBatteryLockedMessage decode(FriendlyByteBuf buffer) {
            return new ManaBatteryLockedMessage(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBoolean());
        }
    }
}
