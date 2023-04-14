package net.yukulab.pointactivity.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.network.packet.play.SendServerConfigBothPacket;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    @Shadow
    public abstract MinecraftServer getServer();

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(
            method = "onPlayerConnect",
            at = @At("HEAD")
    )
    public void initPointContainer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (connection.pointactivity$isModLoaded()) {
            player.pointactivity$initPointContainer();
        }
    }

    @Inject(
            method = "onPlayerConnect",
            at = @At("RETURN")
    )
    public void sendServerData(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (connection.pointactivity$isModLoaded()) {
            SendServerConfigBothPacket.send(player, getServer().pointactivity$getServerConfig());
            player.pointactivity$getPointContainer().ifPresentOrElse(
                    container -> UpdatePointS2CPacket.send(
                            player,
                            container.getPoint(),
                            container.getReasonCache(),
                            container.isShadowMode()
                    ),
                    () ->
                            PointActivity.LOGGER.warn("Unexpected error.", new RuntimeException("Container is null"))
            );
        }
    }

    @Inject(
            method = "respawnPlayer",
            at = @At("RETURN")
    )
    private void applyDeathPenalty(
            ServerPlayerEntity player,
            boolean alive,
            CallbackInfoReturnable<ServerPlayerEntity> cir
    ) {
        if (player.isSpectator()) {
            return;
        }
        cir.getReturnValue().pointactivity$getPointContainer().ifPresent(container -> {
            var penalty = server.pointactivity$getServerConfig().deathPenalty();
            ((ServerPointContainer) container).subtractPoint(penalty, PointReason.RESPAWN);
        });
    }
}
