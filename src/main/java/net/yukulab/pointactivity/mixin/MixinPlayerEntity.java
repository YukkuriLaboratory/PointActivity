package net.yukulab.pointactivity.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.extension.MovingCounter;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements MovingCounter {
    Vec3d preventPrevPos;

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
        var actualMove = getPreventPrevPos()
                .map(vec -> new Vec3d(player.prevX, player.prevY, player.prevZ).subtract(vec))
                .orElse(Vec3d.ZERO);
        if (player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.isPartOfGame()) {
            var server = serverPlayerEntity.server;
            var movedCm = Math.round((float) Math.sqrt(dx * dx + dz * dz) * 100F);
            var actualMoveX = actualMove.getX();
            var actualMoveZ = actualMove.getZ();
            var actualMovedCm =
                    Math.round((float) Math.sqrt(actualMoveX * actualMoveX + actualMoveZ * actualMoveZ) * 100F);
            if (movedCm > 0 && actualMovedCm > 0) {
                var moveHoriPointPer = server.pointactivity$getServerConfig().moveHorizontalPointPer();
                moveCmHolder += movedCm;
                if (moveCmHolder > moveHoriPointPer) {
                    var walkedPoint = moveCmHolder / moveHoriPointPer;
                    serverPlayerEntity.pointactivity$getPointContainer()
                            .ifPresent(container ->
                                    ((ServerPointContainer) container).addPoint(walkedPoint, PointReason.MOVE)
                            );
                    PointActivity.LOGGER.debug("Player:{} MoveH:{}", serverPlayerEntity.getEntityName(), walkedPoint);
                    moveCmHolder %= moveHoriPointPer;
                }
            }
            if (dy > 0 && actualMove.getY() > 0) {
                var moveVertPointPer = server.pointactivity$getServerConfig().moveVerticalPointPer();
                var climbedCm = Math.round(dy * 100.0);
                climbCmHolder += climbedCm;
                if (climbCmHolder > moveVertPointPer) {
                    var climbedPoint = climbCmHolder / moveVertPointPer;
                    serverPlayerEntity.pointactivity$getPointContainer()
                            .ifPresent(container ->
                                    ((ServerPointContainer) container).addPoint(climbedPoint, PointReason.MOVE)
                            );
                    PointActivity.LOGGER.debug("Player:{} MoveV:{}", serverPlayerEntity.getEntityName(), climbedPoint);
                    climbCmHolder %= moveVertPointPer;
                }
            }
        }
        resetPreventPrevPos();
    }

    @Inject(
            method = "dismountVehicle",
            at = @At("RETURN")
    )
    private void resetPrevPos(CallbackInfo ci) {
        resetPreventPrevPos();
    }

    @Override
    public void resetPreventPrevPos() {
        var player = (PlayerEntity) (Object) this;
        setPreventPrevPos(new Vec3d(player.prevX, player.prevY, player.prevZ));
    }

    public void setPreventPrevPos(Vec3d newPrevPos) {
        preventPrevPos = newPrevPos;
    }

    public Optional<Vec3d> getPreventPrevPos() {
        return Optional.ofNullable(preventPrevPos);
    }
}
