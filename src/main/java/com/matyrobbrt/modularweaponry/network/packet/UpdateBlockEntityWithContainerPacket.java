package com.matyrobbrt.modularweaponry.network.packet;

import com.matyrobbrt.modularweaponry.api.be.WithContainerBlockEntity;
import com.matyrobbrt.modularweaponry.api.network.Packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent.Context;

public record UpdateBlockEntityWithContainerPacket(BlockPos pos) implements Packet {

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(Context context) {
        if (context.getSender().level.getBlockEntity(pos) instanceof WithContainerBlockEntity wc) {
            wc.containerChanged();
        }
    }

    public static UpdateBlockEntityWithContainerPacket decode(FriendlyByteBuf buf) {
        return new UpdateBlockEntityWithContainerPacket(buf.readBlockPos());
    }

}
