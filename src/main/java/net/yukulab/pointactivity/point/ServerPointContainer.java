package net.yukulab.pointactivity.point;

import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;

public class ServerPointContainer extends PointContainer {
    private final ServerPlayerEntity player;

    public ServerPointContainer(ServerPlayerEntity playerEntity) {
        player = playerEntity;
    }

    public void subtractPoint(int amount, PointReason reason) {
        addReasonPoint(reason, amount);
        subtractPoint(amount);
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
        UpdatePointS2CPacket.send(player, currentPoint, reasonCache);
    }
}
