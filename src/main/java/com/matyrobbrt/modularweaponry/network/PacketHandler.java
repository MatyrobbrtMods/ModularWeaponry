package com.matyrobbrt.modularweaponry.network;

import com.matyrobbrt.modularweaponry.api.network.BasePacketHandler;
import com.matyrobbrt.modularweaponry.network.packet.ChangeActiveModulePacket;
import com.matyrobbrt.modularweaponry.network.packet.UpdateBlockEntityWithContainerPacket;
import com.matyrobbrt.modularweaponry.network.packet.UpdateModuleBenchPacket;

import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler extends BasePacketHandler {

    private final SimpleChannel channel;

    public PacketHandler(SimpleChannel channel) {
        this.channel = channel;
    }

    @Override
    protected SimpleChannel getChannel() {
        return channel;
    }

    @Override
    public void initialize() {
        registerClientToServer(ChangeActiveModulePacket.class, ChangeActiveModulePacket::decode);
        registerClientToServer(UpdateBlockEntityWithContainerPacket.class,
            UpdateBlockEntityWithContainerPacket::decode);

        registerServerToClient(UpdateModuleBenchPacket.class, UpdateModuleBenchPacket::decode);
    }
}
