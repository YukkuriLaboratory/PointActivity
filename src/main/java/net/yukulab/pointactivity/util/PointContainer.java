package net.yukulab.pointactivity.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;

import java.time.Duration;
import java.time.Instant;

/**
 * 行動ポイントを保持するクラス
 */
public class PointContainer {
    @Environment(EnvType.SERVER)
    private final ServerPlayerEntity player;
    private int currentPoint;

    @Environment(EnvType.CLIENT)
    private int currentCombo;

    @Environment(EnvType.CLIENT)
    private Instant lastComboTime;

    /**
     * コンボの継続時間
     * これは後々コンフィグとかで変更できるようにする
     */
    @Environment(EnvType.CLIENT)
    private static final int CONTINUE_TIME_MILLIS = 1500;

    @Environment(EnvType.SERVER)
    public PointContainer(ServerPlayerEntity playerEntity) {
        player = playerEntity;
    }

    @Environment(EnvType.CLIENT)
    public PointContainer() {
        player = null;
    }

    public void subtractPoint(int amount) {
        if (currentPoint < amount) {
            currentPoint = 0;
        } else {
            currentPoint -= amount;
        }
        if (!isClientSide()) {
            sendUpdatePacket();
        }
    }

    public void setPoint(int point) {
        if (point < 0) {
            throw new IllegalArgumentException("Point must not be negative amount!");
        }
        if (point == currentPoint) {
            return;
        }

        if (isClientSide()) {
            currentCombo = point;
            lastComboTime = Instant.now();
        } else {
            currentPoint = point;
            sendUpdatePacket();
        }
    }

    public int getPoint() {
        return currentPoint;
    }

    public boolean hasPoint() {
        return currentPoint > 0;
    }

    @Environment(EnvType.CLIENT)
    public void tick() {
        var now = Instant.now();
        if (currentCombo != 0 && Duration.between(lastComboTime, now).toMillis() > CONTINUE_TIME_MILLIS) {
            subtractPoint(currentCombo);
            currentCombo = 0;
        }
    }

    private boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Environment(EnvType.SERVER)
    private void sendUpdatePacket() {
        UpdatePointS2CPacket.send(player, currentPoint);
    }
}
