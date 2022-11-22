package net.yukulab.pointactivity.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("PointActivity Config"));
            var serverCategory = builder.getOrCreateCategory(Text.literal("Server"));
            var clientCategory = builder.getOrCreateCategory(Text.literal("Client"));
            return builder.build();
        };
    }
}
