package net.yukulab.pointactivity.network.packet.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.Networking;

public class UpdatePointS2CPacket {
    protected UpdatePointS2CPacket() {
        throw new UnsupportedOperationException("Do not call me");
    }

    @Environment(EnvType.SERVER)
    public static void send(ServerPlayerEntity player, int currentPoint) {
        var buf = PacketByteBufs.create();
        buf.writeInt(currentPoint);
        ServerPlayNetworking.send(player, Networking.UPDATE_POINT, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void onReceive(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseSender
    ) {
        var currentPoint = buf.readInt();
        client.pointactivity$getPointContainer().ifPresent(container -> container.setPoint(currentPoint));
    }
}
