package net.yukulab.pointactivity.mixin;

import io.github.takusan23.clickmanaita.item.ClickManaitaCustomItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClickManaitaCustomItem.class)
public abstract class MixinClickManaitaCustomItem {
    @Inject(
            method = "useOnBlock",
            at = @At("RETURN")
    )
    private void consumeUseManaita(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context != null && context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
            var server = serverPlayer.server;
            var manaitaHandPoint = server.pointactivity$getServerConfig().manaitaHandPoint();
            serverPlayer.pointactivity$getPointContainer()
                    .ifPresent(container ->
                            ((ServerPointContainer) container).addPoint(manaitaHandPoint, PointReason.MANAITA)
                    );
        }
    }
}
