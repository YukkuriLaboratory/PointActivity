package net.yukulab.pointactivity.point;

public enum PointReason {
    NONE(""), MOVE("移動"), CRAFT("クラフト"), SWING("スイング"), ATTACK("攻撃"), BOW("弓"), EAT("飲食"), RESPAWN("リスポーン"), MANAITA("まな板");

    public final String displayName;

    PointReason(String name) {
        displayName = name;
    }
}
