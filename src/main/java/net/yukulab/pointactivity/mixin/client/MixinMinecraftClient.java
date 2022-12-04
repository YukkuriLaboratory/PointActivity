package net.yukulab.pointactivity.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.yukulab.pointactivity.config.ClientConfig;
import net.yukulab.pointactivity.config.ConfigIO;
import net.yukulab.pointactivity.config.ServerConfig;
import net.yukulab.pointactivity.extension.ClientConfigHolder;
import net.yukulab.pointactivity.extension.ModLoadedFlagHolder;
import net.yukulab.pointactivity.extension.PointHolder;
import net.yukulab.pointactivity.point.ClientPointContainer;
import net.yukulab.pointactivity.point.PointContainer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
        implements ModLoadedFlagHolder, PointHolder, ClientConfigHolder {
    private boolean connectedModdedServer;
    private ClientPointContainer pointContainer;

    private ClientConfig clientConfig =
            ConfigIO.readConfig(ClientConfig.class).orElseGet(() -> {
                var config = ClientConfig.getAsDefault();
                ConfigIO.writeConfig(config);
                return config;
            });

    private ServerConfig serverConfig;

    /**
     * @return 本MODが導入されたサーバーに接続してるかどうか
     */
    @Override
    public boolean pointactivity$isModLoaded() {
        return connectedModdedServer;
    }

    @Override
    public Optional<PointContainer> pointactivity$getPointContainer() {
        return Optional.ofNullable(pointContainer);
    }

    @Override
    public void pointactivity$initPointContainer() {
        if (pointContainer != null) {
            throw new IllegalStateException("PointContainer already be initialized!");
        }
        pointContainer = new ClientPointContainer();
    }

    @Override
    public ClientConfig pointactivity$getClientConfig() {
        return clientConfig;
    }

    @Override
    public void pointactivity$setClientConfig(@NotNull ClientConfig config) {
        if (!config.equals(this.clientConfig)) {
            this.clientConfig = config;
            ConfigIO.writeConfig(config);
        }
    }

    @Override
    public Optional<ServerConfig> pointactivity$getServerConfig() {
        return Optional.ofNullable(serverConfig);
    }

    @Override
    public void pointactivity$setServerConfig(@NotNull ServerConfig config) {
        serverConfig = config;
    }

    @Override
    public void pointactivity$onModLoaded() {
        connectedModdedServer = true;
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void initPointContainer(RunArgs args, CallbackInfo ci) {
        pointactivity$initPointContainer();
    }

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void tickPointContainer(CallbackInfo ci) {
        pointContainer.tick();
    }

    @Inject(
            method = "disconnect()V",
            at = @At("RETURN")
    )
    private void resetModFlag(CallbackInfo ci) {
        connectedModdedServer = false;
    }

    @Inject(
            method = "disconnect()V",
            at = @At("RETURN")
    )
    private void resetServerConfig(CallbackInfo ci) {
        serverConfig = null;
    }

    @Inject(
            method = "doAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"
            ),
            cancellable = true
    )
    private void checkPoint(CallbackInfoReturnable<Boolean> cir) {
        if (!pointContainer.hasPoint()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "handleBlockBreaking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"
            ),
            cancellable = true
    )
    private void checkBlockBreakable(boolean breaking, CallbackInfo ci) {
        if (!pointContainer.hasPoint()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "doItemUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkItemUsable(CallbackInfo ci) {
        if (!pointContainer.hasPoint()) {
            ci.cancel();
        }
    }
}
