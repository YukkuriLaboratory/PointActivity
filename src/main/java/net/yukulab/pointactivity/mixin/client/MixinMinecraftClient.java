package net.yukulab.pointactivity.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.yukulab.pointactivity.config.ClientConfig;
import net.yukulab.pointactivity.config.ConfigIO;
import net.yukulab.pointactivity.mixin.extension.ClientConfigHolder;
import net.yukulab.pointactivity.mixin.extension.PointHolder;
import net.yukulab.pointactivity.point.ClientPointContainer;
import net.yukulab.pointactivity.point.PointContainer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements PointHolder, ClientConfigHolder {
    @SuppressWarnings("checkstyle:membername")
    private ClientPointContainer pointactivity$pointContainer;

    @SuppressWarnings("checkstyle:membername")
    private ClientConfig pointactivity$config =
            ConfigIO.readConfig(ClientConfig.class).orElseGet(() -> {
                var config = ClientConfig.getAsDefault();
                ConfigIO.writeConfig(config);
                return config;
            });

    @Override
    public Optional<PointContainer> getPointContainer() {
        return Optional.ofNullable(pointactivity$pointContainer);
    }

    @Override
    public void initPointContainer() {
        if (pointactivity$pointContainer != null) {
            throw new IllegalStateException("PointContainer already be initialized!");
        }
        pointactivity$pointContainer = new ClientPointContainer();
    }

    @Override
    public ClientConfig pointactivity$getConfig() {
        return pointactivity$config;
    }

    @Override
    public void pointactivity$setConfig(@NotNull ClientConfig config) {
        if (!config.equals(pointactivity$config)) {
            pointactivity$config = config;
            ConfigIO.writeConfig(config);
        }
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void initPointContainer(RunArgs args, CallbackInfo ci) {
        initPointContainer();
    }

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void tickPointContainer(CallbackInfo ci) {
        pointactivity$pointContainer.tick();
    }
}
