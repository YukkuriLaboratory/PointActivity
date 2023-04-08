package net.yukulab.pointactivity.network.packet;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.yukulab.pointactivity.network.Networking;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class HandShakeS2CPacket {
    protected HandShakeS2CPacket() {
        throw new UnsupportedOperationException("Do not call me");
    }

    public static void sendQuery(
            ServerLoginNetworkHandler handler,
            MinecraftServer server,
            PacketSender sender,
            ServerLoginNetworking.LoginSynchronizer synchronizer
    ) {
        sender.sendPacket(Networking.HANDSHAKE, PacketByteBufs.empty());
    }

    public static void onHandShakeServer(
            MinecraftServer server,
            ServerLoginNetworkHandler handler,
            boolean understood, PacketByteBuf buf,
            ServerLoginNetworking.LoginSynchronizer synchronizer,
            PacketSender responseSender
    ) {
        if (!understood) {
            return;
        }
        handler.connection.pointactivity$onModLoaded();
    }

    @Environment(EnvType.CLIENT)
    public static CompletableFuture<PacketByteBuf> onHandShakeClient(
            MinecraftClient client,
            ClientLoginNetworkHandler clientLoginNetworkHandler,
            PacketByteBuf buf,
            Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer
    ) {
        client.pointactivity$onModLoaded();
        return CompletableFuture.completedFuture(PacketByteBufs.empty());
    }
}
