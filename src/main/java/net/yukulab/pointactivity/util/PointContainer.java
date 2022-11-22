package net.yukulab.pointactivity.util;

/**
 * 行動ポイントを保持するクラス
 */
public class PointContainer {
    private int currentPoint;

    public void substractPoint(int amount) {
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
        currentPoint = point;
    }

    public int getPoint() {
        return currentPoint;
    }

    public boolean hasPoint() {
        return currentPoint > 0;
    }
}
