package net.yukulab.pointactivity.mixin;

import io.github.takusan23.clickmanaita.block.ClickManaitaBaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClickManaitaBaseBlock.class)
public abstract class MixinClickManaitaBaseBlock {
    @Inject(
            method = "onUse",
            at = @At("RETURN")
    )
    private void consumeUseManaitaBlock(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            var server = serverPlayer.server;
            var manaitaBlockPoint = server.pointactivity$getServerConfig().manaitaBlockPoint();
            serverPlayer.pointactivity$getPointContainer()
                    .ifPresent(container ->
                            ((ServerPointContainer) container).addPoint(manaitaBlockPoint, PointReason.MANAITA)
                    );
        }
    }
}
