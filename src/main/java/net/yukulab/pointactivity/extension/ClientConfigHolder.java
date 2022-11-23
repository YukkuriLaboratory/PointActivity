package net.yukulab.pointactivity.extension;

import net.yukulab.pointactivity.config.ClientConfig;
import net.yukulab.pointactivity.config.ServerConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("checkstyle:MethodName")
public interface ClientConfigHolder {
    default ClientConfig pointactivity$getClientConfig() {
        return null;
    }

    default void pointactivity$setClientConfig(@NotNull ClientConfig config) {
    }

    default Optional<ServerConfig> pointactivity$getServerConfig() {
        return Optional.empty();
    }

    default void pointactivity$setServerConfig(@NotNull ServerConfig config) {
    }
}
