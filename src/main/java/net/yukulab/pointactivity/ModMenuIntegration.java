package net.yukulab.pointactivity;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.yukulab.pointactivity.config.ClientConfig;

import java.util.concurrent.atomic.AtomicInteger;

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
            var serverCategory = builder.getOrCreateCategory(Text.literal("Server"));

            var clientCategory = builder.getOrCreateCategory(Text.literal("Client"));
            var entryBuilder = builder.entryBuilder();
            clientCategory.addEntry(
                    entryBuilder.startIntField(Text.literal("Combo継続時間(ミリ秒)"), clientConfig.comboContinueTimeMillis())
                            .setDefaultValue(clientDefaultConfig.comboContinueTimeMillis())
                            .setMin(0)
                            .setSaveConsumer(comboContinueTimeMillis::set)
                            .build()
            );
            builder.setSavingRunnable(() -> {
                var newClientConfig = new ClientConfig(comboContinueTimeMillis.get());
                setClientConfig(newClientConfig);
            });
            return builder.build();
        };
    }

    private ClientConfig getClientConfig() {
        return MinecraftClient.getInstance().pointactivity$getConfig();
    }

    private void setClientConfig(ClientConfig config) {
        MinecraftClient.getInstance().pointactivity$setConfig(config);
    }
}
