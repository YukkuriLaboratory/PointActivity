package net.yukulab.pointactivity.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot {
    @Shadow
    @Final
    private PlayerEntity player;

    @SuppressWarnings("checkstyle:LineLength")
    @Inject(
            method = "onCrafted(Lnet/minecraft/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;onCraft(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V"
            )
    )
    private void consumeCraftedPoint(ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isPartOfGame()) {
            var server = serverPlayer.server;
            var craftPoint = server.pointactivity$getServerConfig().craftPoint();
            serverPlayer.pointactivity$getPointContainer()
                    .ifPresent(container ->
                            ((ServerPointContainer) container).subtractPoint(craftPoint, PointReason.CRAFT)
                    );
        }
    }
}
