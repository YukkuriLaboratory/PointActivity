package net.yukulab.pointactivity.point;

public enum PointReason {
    NONE(""), MOVE("移動"), CRAFT("クラフト"), SWING("スイング"), ATTACK("攻撃");

    public final String displayName;

    PointReason(String name) {
        displayName = name;
    }
}
