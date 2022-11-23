package net.yukulab.pointactivity.mixin.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.PointActivity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
@Environment(EnvType.SERVER)
public abstract class MixinPlayerEntity {
    int moveCmHolder;
    int climbCmHolder;

    @Inject(
            method = "increaseTravelMotionStats",
            at = @At("TAIL")
    )
    private void consumeWalkPoint(double dx, double dy, double dz, CallbackInfo ci) {
        var player = (PlayerEntity) (Object) this;
        if (player.hasVehicle()) {
            return;
        }

        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            var server = (MinecraftDedicatedServer) serverPlayerEntity.server;

            var movedCm = Math.round((float) Math.sqrt(dx * dx + dz * dz) * 100.0F);

            if (movedCm > 0) {
                var moveHoriPointPer = server.pointactivity$getServerConfig().moveHorizontalPointPer();
                moveCmHolder += movedCm;
                if (moveCmHolder > moveHoriPointPer) {
                    var walkedPoint = moveCmHolder / moveHoriPointPer;
                    serverPlayerEntity.pointactivity$getPointContainer()
                            .ifPresent(container -> container.subtractPoint(walkedPoint));
                    moveCmHolder %= moveHoriPointPer;
                }
            }
            if (dy > 0) {
                var moveVertPointPer = server.pointactivity$getServerConfig().moveVerticalPointPer();
                var climbedCm = Math.round(dy * 100.0);
                climbCmHolder += climbedCm;
                if (climbCmHolder > moveVertPointPer) {
                    var climbedPoint = climbCmHolder / moveVertPointPer;
                    serverPlayerEntity.pointactivity$getPointContainer()
                            .ifPresent(container -> container.subtractPoint(climbedPoint));
                    climbCmHolder %= moveVertPointPer;
                }
            }
        } else {
            PointActivity.LOGGER.error("Unexpected error", new RuntimeException("player must be ServerPlayerEntity"));
        }
    }
}
