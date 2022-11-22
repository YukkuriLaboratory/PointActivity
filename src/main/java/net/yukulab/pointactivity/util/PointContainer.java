package net.yukulab.pointactivity.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;

/**
 * 行動ポイントを保持するクラス
 */
public class PointContainer {
    private final ServerPlayerEntity player;
    private int currentPoint;

    public PointContainer(ServerPlayerEntity player) {
        this.player = player;
    }

    public void substractPoint(int amount) {
        if (currentPoint < amount) {
            currentPoint = 0;
        } else {
            currentPoint -= amount;
        }
        sendUpdatePacket();
    }

    public void setPoint(int point) {
        if (point < 0) {
            throw new IllegalArgumentException("Point must not be negative amount!");
        }
        if (point == currentPoint) {
            return;
        }

        currentPoint = point;
        sendUpdatePacket();
    }

    public int getPoint() {
        return currentPoint;
    }

    public boolean hasPoint() {
        return currentPoint > 0;
    }

    private void sendUpdatePacket() {
        new UpdatePointS2CPacket(currentPoint).send(player);
    }
}
