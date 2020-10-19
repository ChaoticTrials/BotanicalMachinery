package de.melanx.botanicalmachinery.network;

import net.minecraft.network.PacketBuffer;

public interface PacketHandler<T> {

    Class<T> messageClass();
    void encode(T msg, PacketBuffer buffer);
    T decode(PacketBuffer buffer);
}
