package net.yukulab.pointactivity.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.yukulab.pointactivity.config.ConfigIO;
import net.yukulab.pointactivity.config.ServerConfig;
import net.yukulab.pointactivity.extension.ServerConfigHolder;
import net.yukulab.pointactivity.network.packet.play.SendServerConfigBothPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ServerConfigHolder {
    @Shadow
    public abstract PlayerManager getPlayerManager();

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
