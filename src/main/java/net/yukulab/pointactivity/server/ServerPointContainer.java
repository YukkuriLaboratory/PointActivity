package net.yukulab.pointactivity.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;
import net.yukulab.pointactivity.util.PointContainer;

@Environment(EnvType.SERVER)
public class ServerPointContainer extends PointContainer {
    @Environment(EnvType.SERVER)
    private final ServerPlayerEntity player;

    public ServerPointContainer(ServerPlayerEntity playerEntity) {
        player = playerEntity;
    }

    @Override
    public void subtractPoint(int amount) {
        super.subtractPoint(amount);
        sendUpdatePacket();
    }

    @Override
    public void setPoint(int point) {
        super.setPoint(point);
        if (point != currentPoint) {
            currentPoint = point;
            sendUpdatePacket();
        }
    }

    private void sendUpdatePacket() {
        UpdatePointS2CPacket.send(player, currentPoint);
    }
}
