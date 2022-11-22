package net.yukulab.pointactivity.point;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.time.Duration;
import java.time.Instant;

@Environment(EnvType.CLIENT)
public class ClientPointContainer extends PointContainer {
    private int currentCombo;

    private Instant lastComboTime;

    /**
     * コンボの継続時間
     * これは後々コンフィグとかで変更できるようにする
     */
    private static final int CONTINUE_TIME_MILLIS = 1500;

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
        currentCombo = currentPoint - point;
        lastComboTime = Instant.now();
    }
}
