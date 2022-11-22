package net.yukulab.pointactivity.network.packet.play;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class UpdatePointS2CPacket extends CustomPlayPacket {
    static {
        NAME = id("updatepoint");
    }

    final int currentPoint;

    public UpdatePointS2CPacket(int point) {
        currentPoint = point;
    }

    public UpdatePointS2CPacket(PacketByteBuf buf) {
        currentPoint = buf.readInt();
    }

    @Override
    public void send(ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        buf.writeInt(currentPoint);
        ServerPlayNetworking.send(player, NAME, buf);
    }
}
