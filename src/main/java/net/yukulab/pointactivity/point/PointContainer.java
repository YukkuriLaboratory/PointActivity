package net.yukulab.pointactivity.point;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 行動ポイントを保持するクラス
 */
public abstract class PointContainer {
    protected int currentPoint;

    protected Map<PointReason, Integer> reasonCache = Maps.newHashMap();

    public void subtractPoint(int amount) {
        if (currentPoint < amount) {
            currentPoint = 0;
        } else {
            currentPoint -= amount;
        }
    }

    public void setPoint(int point) {
        if (point < 0) {
            throw new IllegalArgumentException("Point must not be negative amount!");
        }
    }

    public int getPoint() {
        return currentPoint;
    }

    public Map<PointReason, Integer> getReasonCache() {
        return new HashMap<>(reasonCache);
    }

    public void addReasonPoint(PointReason pointReason, int amount) {
        var current = reasonCache.getOrDefault(pointReason, 0);
        reasonCache.put(pointReason, current + amount);
    }

    public boolean hasPoint() {
        return currentPoint > 0;
    }


    private boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
