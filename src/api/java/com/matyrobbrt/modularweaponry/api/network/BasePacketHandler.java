package com.matyrobbrt.modularweaponry.api.network;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class BasePacketHandler {

    public static SimpleChannel createChannel(ResourceLocation name,
        com.matyrobbrt.modularweaponry.api.utils.Version version) {
        String protocolVersion = version.toString();
        return NetworkRegistry.ChannelBuilder.named(name).clientAcceptedVersions(protocolVersion::equals)
            .serverAcceptedVersions(protocolVersion::equals).networkProtocolVersion(() -> protocolVersion)
            .simpleChannel();
    }

    private int index = 0;

    protected abstract SimpleChannel getChannel();

    public abstract void initialize();

    protected <MSG extends Packet> void registerClientToServer(Class<MSG> type,
        Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    protected <MSG extends Packet> void registerServerToClient(Class<MSG> type,
        Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    private <MSG extends Packet> void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder,
        NetworkDirection networkDirection) {
        getChannel().registerMessage(index++, type, Packet::encode, decoder, Packet::handle,
            Optional.of(networkDirection));
    }

    /**
     * Send this message to the specified player.
     *
     * @param message - the message to send
     * @param player  - the player to send it to
     */
    public <MSG> void sendTo(MSG message, ServerPlayer player) {
        // Validate it is not a fake player, even though none of our code should call
        // this with a fake player
        if (!(player instanceof FakePlayer)) {
            getChannel().sendTo(message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    /**
     * Send this message to everyone connected to the server.
     *
     * @param message - message to send
     */
    public <MSG> void sendToAll(MSG message) {
        getChannel().send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send this message to everyone connected to the server if the server has
     * loaded.
     *
     * @param   message - message to send
     *
     * @apiNote         This is useful for reload listeners
     */
    public <MSG> void sendToAllIfLoaded(MSG message) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            // If the server has loaded, send to all players
            sendToAll(message);
        }
    }

    /**
     * Send this message to everyone within the supplied dimension.
     *
     * @param message   - the message to send
     * @param dimension - the dimension to target
     */
    public <MSG> void sendToDimension(MSG message, ResourceKey<Level> dimension) {
        getChannel().send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    /**
     * Send this message to the server.
     *
     * @param message - the message to send
     */
    public <MSG> void sendToServer(MSG message) {
        getChannel().sendToServer(message);
    }

    public <MSG> void sendToAllTracking(MSG message, Entity entity) {
        getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    public <MSG> void sendToAllTrackingAndSelf(MSG message, Entity entity) {
        getChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    public <MSG> void sendToAllTracking(MSG message, BlockEntity tile) {
        sendToAllTracking(message, tile.getLevel(), tile.getBlockPos());
    }

    @SuppressWarnings("resource")
    public <MSG> void sendToAllTracking(MSG message, Level world, BlockPos pos) {
        if (world instanceof ServerLevel level) {
            // If we have a ServerWorld just directly figure out the ChunkPos to not require
            // looking up the chunk
            // This provides a decent performance boost over using the packet distributor
            level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> sendTo(message, p));
        } else {
            // Otherwise, fallback to entities tracking the chunk if some mod did something
            // odd and our world is not a ServerWorld
            getChannel().send(
                PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(pos.getX() >> 4, pos.getZ() >> 4)), message);
        }
    }
}
