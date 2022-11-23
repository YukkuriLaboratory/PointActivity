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

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var clientDefaultConfig = ClientConfig.getAsDefault();
            var clientConfig = getClientConfig();
            var comboContinueTimeMillis = new AtomicInteger(clientConfig.comboContinueTimeMillis());
            var pointAnimationMillis = new AtomicInteger(clientConfig.pointAnimationTimeMillis());

            var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("PointActivity Config"));

            var entryBuilder = builder.entryBuilder();

            var clientCategory = builder.getOrCreateCategory(Text.literal("Client"));
            clientCategory.addEntry(
                    entryBuilder.startIntField(Text.literal("コンボの継続時間(ミリ秒)"), clientConfig.comboContinueTimeMillis())
                            .setDefaultValue(clientDefaultConfig.comboContinueTimeMillis())
                            .setMin(0)
                            .setSaveConsumer(comboContinueTimeMillis::set)
                            .build()
            );
            clientCategory.addEntry(
                    entryBuilder.startIntField(
                                    Text.literal("ポイントのアニメーション時間(ミリ秒)"), clientConfig.pointAnimationTimeMillis()
                            )
                            .setDefaultValue(clientDefaultConfig.comboContinueTimeMillis())
                            .setMin(0)
                            .setSaveConsumer(pointAnimationMillis::set)
                            .build()
            );

            var serverDefaultConfig = ServerConfig.getAsDefault();
            var optionalServerConfig = getServerConfig();
            AtomicInteger moveHoriPointPer = new AtomicInteger();
            AtomicInteger moveVertPointPer = new AtomicInteger();
            if (optionalServerConfig.isPresent()) {
                var serverConfig = optionalServerConfig.get();
                moveHoriPointPer.set(serverConfig.moveHorizontalPointPer());
                moveVertPointPer.set(serverConfig.moveVerticalPointPer());
                var serverCategory = builder.getOrCreateCategory(Text.literal("Server"));
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1ポイントあたりの水平移動可能距離(cm)"), moveHoriPointPer.get())
                                .setTooltip(Text.literal("note: 1ブロック=100cm"))
                                .setDefaultValue(serverDefaultConfig.moveHorizontalPointPer())
                                .setSaveConsumer(moveHoriPointPer::set)
                                .build()
                );
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1ポイントあたりの垂直移動可能距離(cm)"), moveHoriPointPer.get())
                                .setTooltip(Text.literal("note: 1ブロック=100cm"))
                                .setDefaultValue(serverDefaultConfig.moveVerticalPointPer())
                                .setSaveConsumer(moveVertPointPer::set)
                                .build()
                );
            }

            builder.setSavingRunnable(() -> {
                var newClientConfig = new ClientConfig(comboContinueTimeMillis.get(), pointAnimationMillis.get());
                setClientConfig(newClientConfig);

                if (isInGame()) {
                    var newServerConfig = new ServerConfig(moveHoriPointPer.get(), moveVertPointPer.get());
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
