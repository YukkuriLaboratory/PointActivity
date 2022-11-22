package net.yukulab.pointactivity.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 行動ポイントを保持するクラス
 */
public abstract class PointContainer {
    protected int currentPoint;

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
        if (point == currentPoint) {
            return;
        }
    }

    public int getPoint() {
        return currentPoint;
    }

    public boolean hasPoint() {
        return currentPoint > 0;
    }


    private boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
