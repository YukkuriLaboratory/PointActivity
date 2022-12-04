package net.yukulab.pointactivity.mixin.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Environment(EnvType.SERVER)
public abstract class MixinLivingEntity {
    private int currentBowUseTick;
    private int currentFoodUseTick;

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
                    .ifPresent(container ->
                            ((ServerPointContainer) container).subtractPoint(swingPoint, PointReason.SWING)
                    );
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
                    .ifPresent(container ->
                            ((ServerPointContainer) container).subtractPoint(attackPoint, PointReason.ATTACK)
                    );
        }
    }

    @Inject(
            method = "tickActiveItemStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;tickItemStackUsage(Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private void handleBowUse(CallbackInfo ci) {
        var livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof ServerPlayerEntity player) {
            var server = ((MinecraftDedicatedServer) player.server);
            var itemStack = player.getStackInHand(player.getActiveHand());
            if (itemStack.getItem() == Items.BOW) {
                player.pointactivity$getPointContainer().ifPresent(container -> {
                    if (++currentBowUseTick > server.pointactivity$getServerConfig().bowPointPer()) {
                        ((ServerPointContainer) container).subtractPoint(1, PointReason.BOW);
                        currentBowUseTick = 0;
                    }
                });
            }
        }
    }

    @Inject(
            method = "tickItemStackUsage",
            at = @At("HEAD")
    )
    private void handleFoodUse(ItemStack stack, CallbackInfo ci) {
        if (!stack.getItem().isFood()) {
            return;
        }
        var livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof ServerPlayerEntity player) {
            var server = ((MinecraftDedicatedServer) player.server);
            player.pointactivity$getPointContainer().ifPresent(container -> {
                if (++currentFoodUseTick > server.pointactivity$getServerConfig().foodPointPer()) {
                    ((ServerPointContainer) container).subtractPoint(1, PointReason.EAT);
                    currentFoodUseTick = 0;
                }
            });
        }
    }

    @Inject(
            method = "consumeItem",
            at = @At("RETURN")
    )
    private void resetItemUseItem(CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;
        if (!entity.isUsingItem()) {
            currentBowUseTick = 0;
            currentFoodUseTick = 0;
        }
    }
}
