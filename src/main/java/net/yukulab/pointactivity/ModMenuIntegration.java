package net.yukulab.pointactivity;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.yukulab.pointactivity.config.ClientConfig;
import net.yukulab.pointactivity.config.ServerConfig;
import net.yukulab.pointactivity.network.packet.play.SendServerConfigBothPacket;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var clientDefaultConfig = ClientConfig.getAsDefault();
            var clientConfig = getClientConfig();
            var comboContinueTimeMillis = new AtomicInteger(clientConfig.comboContinueTimeMillis());

            var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("PointActivity Config"));

            var entryBuilder = builder.entryBuilder();

            var clientCategory = builder.getOrCreateCategory(Text.literal("Client"));
            clientCategory.addEntry(
                    entryBuilder.startIntField(Text.literal("Combo継続時間(ミリ秒)"), clientConfig.comboContinueTimeMillis())
                            .setDefaultValue(clientDefaultConfig.comboContinueTimeMillis())
                            .setMin(0)
                            .setSaveConsumer(comboContinueTimeMillis::set)
                            .build()
            );

            var serverDefaultConfig = ServerConfig.getAsDefault();
            var optionalServerConfig = getServerConfig();
            AtomicReference<String> dummy = new AtomicReference<>();
            if (optionalServerConfig.isPresent()) {
                var serverConfig = optionalServerConfig.get();
                dummy.set(serverConfig.dummy());
                var serverCategory = builder.getOrCreateCategory(Text.literal("Server"));
                serverCategory.addEntry(
                        entryBuilder.startStrField(Text.literal("Dummy"), dummy.get())
                                .setDefaultValue(serverDefaultConfig.dummy())
                                .setSaveConsumer(dummy::set)
                                .build()
                );
            }

            builder.setSavingRunnable(() -> {
                var newClientConfig = new ClientConfig(comboContinueTimeMillis.get());
                setClientConfig(newClientConfig);

                if (isInGame()) {
                    var newServerConfig = new ServerConfig(dummy.get());
                    SendServerConfigBothPacket.send(newServerConfig);
                } else if (optionalServerConfig.isPresent()) {
                    PointActivity.LOGGER.warn("Failed to apply ServerConfig", new RuntimeException("Connection lost."));
                }
            });
            return builder.build();
        };
    }

    private ClientConfig getClientConfig() {
        return MinecraftClient.getInstance().pointactivity$getClientConfig();
    }

    private void setClientConfig(ClientConfig config) {
        MinecraftClient.getInstance().pointactivity$setClientConfig(config);
    }

    private Optional<ServerConfig> getServerConfig() {
        return MinecraftClient.getInstance().pointactivity$getServerConfig();
    }

    private boolean isInGame() {
        return MinecraftClient.getInstance().pointactivity$isModLoaded();
    }
}
