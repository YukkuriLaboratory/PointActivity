package net.yukulab.pointactivity.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.yukulab.pointactivity.network.packet.HandShakeS2CPacket;

public class Networking {
    protected Networking() {}

    @Environment(EnvType.SERVER)
    public static void registerServerReceivers() {
        ServerLoginConnectionEvents.QUERY_START.register(HandShakeS2CPacket::sendQuery);
        ServerLoginNetworking.registerGlobalReceiver(HandShakeS2CPacket.NAME, HandShakeS2CPacket::onHandShakeServer);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientReceivers() {
        ClientLoginNetworking.registerGlobalReceiver(HandShakeS2CPacket.NAME, HandShakeS2CPacket::onHandShakeClient);
    }

}
