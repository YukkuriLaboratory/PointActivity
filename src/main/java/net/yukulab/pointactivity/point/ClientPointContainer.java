package net.yukulab.pointactivity.point;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.time.Duration;
import java.time.Instant;

@Environment(EnvType.CLIENT)
public class ClientPointContainer extends PointContainer {
    private int currentCombo;

    private Instant lastComboTime;

    public void tick() {
        var now = Instant.now();
        var continueTimeMillis = MinecraftClient.getInstance().pointactivity$getClientConfig().comboContinueTimeMillis();
        if (currentCombo != 0 && Duration.between(lastComboTime, now).toMillis() > continueTimeMillis) {
            subtractPoint(currentCombo);
            currentCombo = 0;
        }
    }

    @Override
    public void setPoint(int point) {
        super.setPoint(point);
        currentCombo = currentPoint - point;
        lastComboTime = Instant.now();
    }
}
