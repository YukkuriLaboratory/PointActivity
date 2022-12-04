package net.yukulab.pointactivity.network.packet.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.config.ServerConfig;
import net.yukulab.pointactivity.network.Networking;

public class SendServerConfigBothPacket {
    protected SendServerConfigBothPacket() {
        throw new UnsupportedOperationException("Do not create this class instance");
    }

    @Environment(EnvType.SERVER)
    public static void send(ServerPlayerEntity player, ServerConfig config) {
        ServerPlayNetworking.send(player, Networking.SEND_CONFIG, convert(config));
    }

    @Environment(EnvType.SERVER)
    public static void onReceive(
            MinecraftServer server,
            ServerPlayerEntity player,
            ServerPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseSender
    ) {
        if (server instanceof MinecraftDedicatedServer dedicatedServer) {
            dedicatedServer.pointactivity$setServerConfig(readConfig(buf));
        } else {
            PointActivity.LOGGER.error("Unexpected error", new RuntimeException("This server is not DedicatedServer."));
        }
    }

    @Environment(EnvType.CLIENT)
    public static void send(ServerConfig config) {
        ClientPlayNetworking.send(Networking.SEND_CONFIG, convert(config));
    }

    @Environment(EnvType.CLIENT)
    public static void onReceive(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseSender
    ) {
        client.pointactivity$setServerConfig(readConfig(buf));
    }

    private static PacketByteBuf convert(ServerConfig config) {
        var buf = PacketByteBufs.create();
        buf.writeInt(config.moveHorizontalPointPer());
        buf.writeInt(config.moveVerticalPointPer());
        buf.writeInt(config.craftPoint());
        buf.writeInt(config.swingHandPoint());
        buf.writeInt(config.attackPoint());
        buf.writeInt(config.bowPointPer());
        buf.writeInt(config.foodPointPer());
        return buf;
    }

    private static ServerConfig readConfig(PacketByteBuf buf) {
        return new ServerConfig(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }
}
