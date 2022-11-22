package net.yukulab.pointactivity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.yukulab.pointactivity.util.PointContainer;

import java.time.Duration;
import java.time.Instant;

@Environment(EnvType.CLIENT)
public class ClientPointContainer extends PointContainer {
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

    @Environment(EnvType.CLIENT)
    public void tick() {
        var now = Instant.now();
        if (currentCombo != 0 && Duration.between(lastComboTime, now).toMillis() > CONTINUE_TIME_MILLIS) {
            subtractPoint(currentCombo);
            currentCombo = 0;
        }
    }

    @Override
    public void setPoint(int point) {
        super.setPoint(point);
        currentCombo = point;
        lastComboTime = Instant.now();
    }
}
