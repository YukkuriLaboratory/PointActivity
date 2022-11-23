package net.yukulab.pointactivity.extension;

import net.yukulab.pointactivity.config.ServerConfig;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("checkstyle:MethodName")
public interface ServerConfigHolder {
    default ServerConfig pointactivity$getServerConfig() {
        return null;
    }

    default void pointactivity$setServerConfig(@NotNull ServerConfig config) {
    }
}
