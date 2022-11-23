package net.yukulab.pointactivity.mixin.server;

import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.yukulab.pointactivity.config.ConfigIO;
import net.yukulab.pointactivity.config.ServerConfig;
import net.yukulab.pointactivity.extension.ServerConfigHolder;
import net.yukulab.pointactivity.network.packet.play.SendServerConfigBothPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinMinecraftDedicatedServer implements ServerConfigHolder {
    @Shadow
    public abstract DedicatedPlayerManager getPlayerManager();

    private ServerConfig serverConfig =
            ConfigIO.readConfig(ServerConfig.class).orElseGet(() -> {
                var config = ServerConfig.getAsDefault();
                ConfigIO.writeConfig(config);
                return config;
            });

    @Override
    public ServerConfig pointactivity$getServerConfig() {
        return serverConfig;
    }

    @Override
    public void pointactivity$setServerConfig(@NotNull ServerConfig config) {
        if (!serverConfig.equals(config)) {
            serverConfig = config;
            ConfigIO.writeConfig(config);
            getPlayerManager().getPlayerList()
                    .forEach(p -> SendServerConfigBothPacket.send(p, config));
        }
    }
}
