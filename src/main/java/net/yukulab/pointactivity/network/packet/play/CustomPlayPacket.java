package net.yukulab.pointactivity.network.packet.play;

import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.CustomPacket;

public abstract class CustomPlayPacket extends CustomPacket {

    abstract void send(ServerPlayerEntity player);
}
