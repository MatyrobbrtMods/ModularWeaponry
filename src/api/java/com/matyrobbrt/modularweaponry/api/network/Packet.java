package com.matyrobbrt.modularweaponry.api.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

public interface Packet {

    void encode(FriendlyByteBuf buffer);

    void handle(NetworkEvent.Context context);

    static <PACKET extends Packet> void handle(PACKET message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> message.handle(context));
        context.setPacketHandled(true);
    }
}
