package net.yukulab.pointactivity.extension;

import net.yukulab.pointactivity.config.ClientConfig;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("checkstyle:MethodName")
public interface ClientConfigHolder {
    default ClientConfig pointactivity$getConfig() {
        return null;
    }

    default void pointactivity$setConfig(@NotNull ClientConfig config) {
    }
}
