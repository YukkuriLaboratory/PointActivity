package net.yukulab.pointactivity.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.yukulab.pointactivity.mixin.extension.PointHolder;
import net.yukulab.pointactivity.util.PointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements PointHolder {
    @SuppressWarnings("checkstyle:membername")
    private PointContainer pointactivity$pointContainer;

    @Override
    public Optional<PointContainer> getPointContainer() {
        return Optional.ofNullable(pointactivity$pointContainer);
    }

    @Override
    public void initPointContainer() {
        if (pointactivity$pointContainer != null) {
            throw new IllegalStateException("PointContainer already be initialized!");
        }
        pointactivity$pointContainer = new PointContainer();
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
