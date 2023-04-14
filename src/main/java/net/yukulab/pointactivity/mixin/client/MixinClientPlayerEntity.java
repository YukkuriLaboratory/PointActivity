package net.yukulab.pointactivity.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.yukulab.pointactivity.point.PointContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(
            method = "move",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkMovable(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        var player = (ClientPlayerEntity) (Object) this;
        if (!client.pointactivity$getPointContainer().map(PointContainer::hasPoint).orElse(true) && player.isPartOfGame()) {
            ci.cancel();
        }
    }
}
