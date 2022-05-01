package com.matyrobbrt.modularweaponry.network.packet;

import com.matyrobbrt.modularweaponry.api.network.Packet;
import com.matyrobbrt.modularweaponry.modulebench.ModuleBenchInfo;
import com.matyrobbrt.modularweaponry.modulebench.ModuleBenchScreen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateModuleBenchPacket implements Packet {

    private ModuleBenchInfo info;
    private Object[] args;

    private Component component;

    private UpdateModuleBenchPacket() {
    }

    public UpdateModuleBenchPacket(ModuleBenchInfo info, Object... args) {
        this.info = info;
        this.args = args;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(info);
        info.encodeArgs(buffer, args);
    }

    @Override
    public void handle(Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ModuleBenchScreen.updateComponent(component));
    }

    public static UpdateModuleBenchPacket decode(FriendlyByteBuf buffer) {
        final var pkt = new UpdateModuleBenchPacket();
        final var info = buffer.readEnum(ModuleBenchInfo.class);
        pkt.component = info.makeComponent(info.decodeArgs(buffer));
        return pkt;
    }
}
