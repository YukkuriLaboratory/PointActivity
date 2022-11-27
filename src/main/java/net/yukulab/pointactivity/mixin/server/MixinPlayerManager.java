package net.yukulab.pointactivity.mixin.server;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.network.packet.play.SendServerConfigBothPacket;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    @Shadow
    public abstract MinecraftServer getServer();

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
            if (getServer() instanceof MinecraftDedicatedServer dedicatedServer) {
                SendServerConfigBothPacket.send(player, dedicatedServer.pointactivity$getServerConfig());
            } else {
                PointActivity.LOGGER.warn("Unexpected error.", new RuntimeException("Server is not DedicatedServer"));
            }
            player.pointactivity$getPointContainer().ifPresentOrElse(
                    container -> UpdatePointS2CPacket.send(player, container.getPoint(), container.getReasonCache()),
                    () ->
                            PointActivity.LOGGER.warn("Unexpected error.", new RuntimeException("Container is null"))
            );
        }
    }
}
