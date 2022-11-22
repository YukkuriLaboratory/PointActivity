package net.yukulab.pointactivity.network.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.yukulab.pointactivity.PointActivity;

public abstract class CustomPacket {
    public static Identifier NAME;

    public CustomPacket(String name) {
        NAME = Identifier.of(PointActivity.MOD_NAME, name);
    }

    protected static void sendPacket(ServerPlayerEntity player, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, NAME, buf);
    }
}
