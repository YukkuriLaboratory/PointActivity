package net.yukulab.pointactivity.mixin.extension;

import net.yukulab.pointactivity.point.PointContainer;

import java.util.Optional;

public interface PointHolder {
    default Optional<PointContainer> getPointContainer() {
        return Optional.empty();
    }

    default void initPointContainer() {}
}
