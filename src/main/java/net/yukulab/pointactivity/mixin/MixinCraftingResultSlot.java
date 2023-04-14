package net.yukulab.pointactivity.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.yukulab.pointactivity.config.ServerConfig;
import net.yukulab.pointactivity.point.PointContainer;
import net.yukulab.pointactivity.point.PointReason;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingResultSlot.class)
public abstract class MixinCraftingResultSlot extends Slot {
    @Shadow
    @Final
    private PlayerEntity player;

    public MixinCraftingResultSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

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

    /**
     * Prevent crafting when player does not have enough point to craft.
     */
    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        var data = getContainerAndCraftPoint();
        var pointContainer = data.getLeft();
        var craftPoint = data.getRight();
        var notHaveEnoughPoint = pointContainer.map(container -> container.getPoint() < craftPoint)
                .orElse(false);
        if (notHaveEnoughPoint) {
            return false;
        }
        return super.canTakeItems(playerEntity);
    }

    private Pair<Optional<PointContainer>, Integer> getContainerAndCraftPoint() {
        Optional<PointContainer> pointContainer;
        int craftPoint;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            pointContainer = serverPlayer.pointactivity$getPointContainer();
            craftPoint = serverPlayer.server.pointactivity$getServerConfig().craftPoint();
        } else {
            pointContainer = MinecraftClient.getInstance().pointactivity$getPointContainer();
            craftPoint = MinecraftClient.getInstance()
                    .pointactivity$getServerConfig()
                    .map(ServerConfig::craftPoint)
                    .orElse(0);
        }
        return new Pair<>(pointContainer, craftPoint);
    }
}
