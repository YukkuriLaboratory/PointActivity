package net.yukulab.pointactivity.point;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;

@Environment(EnvType.SERVER)
public class ServerPointContainer extends PointContainer {
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
            if (player.networkHandler != null) {
                sendUpdatePacket();
            }
        }
    }

    private void sendUpdatePacket() {
        UpdatePointS2CPacket.send(player, currentPoint);
    }
}
