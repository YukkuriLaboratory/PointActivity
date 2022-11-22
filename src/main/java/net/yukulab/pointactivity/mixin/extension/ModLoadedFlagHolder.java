package net.yukulab.pointactivity.mixin.extension;

public interface ModLoadedFlagHolder {
    default boolean isModLoaded() {
        return false;
    }

    default void onModLoaded() {}
}
