package net.yukulab.pointactivity.mixin.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Environment(EnvType.SERVER)
public abstract class MixinLivingEntity {
    @SuppressWarnings("checkstyle:LineLength")
    @Inject(
            method = "swingHand(Lnet/minecraft/util/Hand;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;getChunkManager()Lnet/minecraft/server/world/ServerChunkManager;"
            )
    )
    private void consumeSwingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayerEntity player && hand == Hand.MAIN_HAND) {
            var server = ((MinecraftDedicatedServer) player.server);
            var swingPoint = server.pointactivity$getServerConfig().swingHandPoint();
            player.pointactivity$getPointContainer()
                    .ifPresent(container -> container.subtractPoint(swingPoint));
        }
    }

    @Inject(
            method = "onAttacking",
            at = @At("RETURN")
    )
    private void consumeAttacking(Entity target, CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayerEntity player) {
            var server = ((MinecraftDedicatedServer) player.server);
            var attackPoint = server.pointactivity$getServerConfig().swingHandPoint();
            player.pointactivity$getPointContainer()
                    .ifPresent(container -> container.subtractPoint(attackPoint));
        }
    }
}
