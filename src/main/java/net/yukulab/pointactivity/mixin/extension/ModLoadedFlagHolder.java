package net.yukulab.pointactivity.mixin.extension;

@SuppressWarnings("checkstyle:MethodName")
public interface ModLoadedFlagHolder {
    default boolean pointactivity$isModLoaded() {
        return false;
    }

    default void pointactivity$onModLoaded() {
    }
}
