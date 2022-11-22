package net.yukulab.pointactivity.mixin.extension;

import net.yukulab.pointactivity.point.PointContainer;

import java.util.Optional;

@SuppressWarnings("checkstyle:MethodName")
public interface PointHolder {
    default Optional<PointContainer> pointactivity$getPointContainer() {
        return Optional.empty();
    }

    default void pointactivity$initPointContainer() {
    }
}
