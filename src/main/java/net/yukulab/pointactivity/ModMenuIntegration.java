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
            var moveHoriPointPer = new AtomicInteger();
            var moveVertPointPer = new AtomicInteger();
            var craftPoint = new AtomicInteger();
            var swingPoint = new AtomicInteger();
            var attackPoint = new AtomicInteger();
            var bowPoint = new AtomicInteger();
            var foodPoint = new AtomicInteger();
            if (optionalServerConfig.isPresent()) {
                var serverConfig = optionalServerConfig.get();
                moveHoriPointPer.set(serverConfig.moveHorizontalPointPer());
                moveVertPointPer.set(serverConfig.moveVerticalPointPer());
                craftPoint.set(serverConfig.craftPoint());
                swingPoint.set(serverConfig.swingHandPoint());
                attackPoint.set(serverConfig.attackPoint());
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
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1クラフトあたりのポイント消費量"), craftPoint.get())
                                .setDefaultValue(serverDefaultConfig.craftPoint())
                                .setSaveConsumer(craftPoint::set)
                                .build()
                );
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1振りあたりのポイント消費量"), swingPoint.get())
                                .setDefaultValue(serverDefaultConfig.swingHandPoint())
                                .setSaveConsumer(swingPoint::set)
                                .build()
                );
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1攻撃あたりのポイント消費量"), attackPoint.get())
                                .setTooltip(Text.literal("注意:振りによるポイント消費も加算されます"))
                                .setDefaultValue(serverDefaultConfig.attackPoint())
                                .setSaveConsumer(attackPoint::set)
                                .build()
                );
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1ポイントあたりの弓の引き時間(Tick)"), bowPoint.get())
                                .setDefaultValue(serverDefaultConfig.bowPointPer())
                                .setSaveConsumer(bowPoint::set)
                                .build()
                );
                serverCategory.addEntry(
                        entryBuilder.startIntField(Text.literal("1ポイントあたりの食事時間(Tick)"), foodPoint.get())
                                .setDefaultValue(serverDefaultConfig.foodPointPer())
                                .setSaveConsumer(foodPoint::set)
                                .build()
                );
            }

            builder.setSavingRunnable(() -> {
                var newClientConfig = new ClientConfig(comboContinueTimeMillis.get(), pointAnimationMillis.get());
                setClientConfig(newClientConfig);

                if (isInGame()) {
                    var newServerConfig = new ServerConfig(
                            moveHoriPointPer.get(),
                            moveVertPointPer.get(),
                            craftPoint.get(),
                            swingPoint.get(),
                            attackPoint.get(),
                            bowPoint.get(),
                            foodPoint.get()
                    );
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
