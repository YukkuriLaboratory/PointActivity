package net.yukulab.pointactivity.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.util.Identifier;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.network.packet.HandShakeS2CPacket;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;

public class Networking {
    protected Networking() {
    }

    public static final Identifier HANDSHAKE = id("handshake");
    public static final Identifier UPDATE_POINT = id("updatepoint");

    @Environment(EnvType.SERVER)
    public static void registerServerReceivers() {
        ServerLoginConnectionEvents.QUERY_START.register(HandShakeS2CPacket::sendQuery);
        ServerLoginNetworking.registerGlobalReceiver(HANDSHAKE, HandShakeS2CPacket::onHandShakeServer);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientReceivers() {
        ClientLoginNetworking.registerGlobalReceiver(HANDSHAKE, HandShakeS2CPacket::onHandShakeClient);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_POINT, UpdatePointS2CPacket::onReceive);
    }

    private static Identifier id(String name) {
        return Identifier.of(PointActivity.MOD_NAME, name);
    }
}
