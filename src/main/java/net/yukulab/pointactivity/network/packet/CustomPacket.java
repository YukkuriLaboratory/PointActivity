package net.yukulab.pointactivity.network.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.yukulab.pointactivity.PointActivity;

public abstract class CustomPacket {
    @SuppressWarnings("checkstyle:StaticVariableName")
    public static Identifier NAME;

    protected static void sendPacket(ServerPlayerEntity player, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, NAME, buf);
    }

    protected static Identifier id(String name) {
        return Identifier.of(PointActivity.MOD_NAME, name);
    }
}
