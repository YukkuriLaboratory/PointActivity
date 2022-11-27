package net.yukulab.pointactivity.point;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.yukulab.pointactivity.hud.ComboElement;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientPointContainer extends PointContainer {
    private int currentCombo;

    private Instant lastComboTime;

    private Map<PointReason, Integer> latestReasonCache = Maps.newHashMap();

    public void tick() {
        var now = Instant.now();
        var continueTimeMillis = MinecraftClient.getInstance()
                .pointactivity$getClientConfig()
                .comboContinueTimeMillis();
        if (currentCombo != 0 && Duration.between(lastComboTime, now).toMillis() > continueTimeMillis) {
            subtractPoint(currentCombo);
            currentCombo = 0;
            ComboElement.INSTANCE.visible = false;
            latestReasonCache = reasonCache;
        }
    }

    @Override
    public void setPoint(int point) {
        super.setPoint(point);
        currentCombo = currentPoint - point;
        lastComboTime = Instant.now();
        ComboElement.INSTANCE.visible = point != 0;
    }

    public void updateReasonCache(Map<PointReason, Integer> newCache) {
        reasonCache = newCache;
    }

    public Map<PointReason, Integer> getCacheDiff() {
        Map<PointReason, Integer> result = Maps.newHashMap();
        latestReasonCache.forEach((reason, amount) -> {
            var currentAmount = reasonCache.getOrDefault(reason, 0) - amount;
            if (currentAmount > 0) {
                result.put(reason, currentAmount);
            }
        });
        return result;
    }
}
