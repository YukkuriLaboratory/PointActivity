package net.yukulab.pointactivity.network.packet.play;

import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.CustomPacket;

public abstract class CustomPlayPacket extends CustomPacket {
    public CustomPlayPacket(String name) {
        super(name);
    }
    
    abstract void send(ServerPlayerEntity player);
}
