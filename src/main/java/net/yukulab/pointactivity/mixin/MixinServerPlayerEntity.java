package net.yukulab.pointactivity.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.extension.PointHolder;
import net.yukulab.pointactivity.point.PointContainer;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements PointHolder {
    @Shadow
    @Final
    public MinecraftServer server;

    private static final String POINT_TAG = String.format("%s$pointcontainer", PointActivity.MOD_NAME);

    private ServerPointContainer pointContainer;

    @Override
    public Optional<PointContainer> pointactivity$getPointContainer() {
        return Optional.ofNullable(pointContainer);
    }

    @Override
    public void pointactivity$initPointContainer() {
        if (pointactivity$getPointContainer().isPresent()) {
            throw new IllegalStateException("PointContainer already initialized!");
        }
        var player = (ServerPlayerEntity) (Object) this;
        pointContainer = new ServerPointContainer(player);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("RETURN")
    )
    public void writePointContainerData(NbtCompound nbt, CallbackInfo ci) {
        pointactivity$getPointContainer().ifPresent(container ->
                nbt.putInt(POINT_TAG, container.getPoint())
        );
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("RETURN")
    )
    public void readPointContainerData(NbtCompound nbt, CallbackInfo ci) {
        pointactivity$getPointContainer().ifPresent(container -> {
            var actionPoint = nbt.getInt(POINT_TAG);
            container.setPoint(actionPoint);
        });
    }

    @Inject(
            method = "copyFrom",
            at = @At("RETURN")
    )
    public void copyPoint(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        oldPlayer.pointactivity$getPointContainer()
                .ifPresent(container -> pointContainer = ((ServerPointContainer) container));
    }

    @Inject(
            method = "changeGameMode",
            at = @At("RETURN")
    )
    public void disableShadowMode(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        var isShadowMode = pointactivity$getPointContainer().map(PointContainer::isShadowMode).orElse(false);
        if (gameMode != GameMode.SPECTATOR && isShadowMode) {
            var player = (ServerPlayerEntity) (Object) this;
            pointContainer.setShadowMode(false);
            server.getPlayerManager().getPlayerList().forEach(target -> {
                if (target == player) {
                    return;
                }
                target.pointactivity$getPointContainer().ifPresent(container -> container.removeShadowedPlayer(player));
            });
        }
    }

    @Inject(
            method = "moveToWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAfterTeleport(DDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private void resetMoveCounter(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        var player = (PlayerEntity) (Object) this;
        player.resetPreventPrevPos();
    }
}
