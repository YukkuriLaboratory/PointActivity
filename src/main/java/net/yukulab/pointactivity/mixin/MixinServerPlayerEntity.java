package net.yukulab.pointactivity.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.extension.PointHolder;
import net.yukulab.pointactivity.point.PointContainer;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements PointHolder {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract void sendMessage(Text message, boolean overlay);

    @Shadow
    @Nullable
    public abstract BlockPos getSpawnPointPosition();

    @Shadow
    public abstract float getSpawnAngle();

    @Shadow
    public abstract RegistryKey<World> getSpawnPointDimension();

    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    private static final String POINT_TAG = String.format("%s$pointcontainer", PointActivity.MOD_NAME);

    private ServerPointContainer pointContainer;
    private int returnCount = 0;
    private boolean returned = true;

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
            method = "tick",
            at = @At("RETURN")
    )
    private void countReturnSecond(CallbackInfo ci) {
        var player = (ServerPlayerEntity) (Object) this;
        if (!pointactivity$getPointContainer().map(PointContainer::hasPoint).orElse(true) && player.isPartOfGame()) {
            if (returnCount == 0 && !returned) {
                teleportToRespawnPoint();
                returned = true;
            } else if (returnCount-- > 10) {
                var remainSec = returnCount / 20;
                var text = Text.literal(String.format("あと%d秒で帰還します", remainSec));
                sendMessage(text, true);
            }
        } else {
            returned = false;
            returnCount = server.pointactivity$getServerConfig().returnCountSec() * 20;
        }
    }

    /**
     * Original {@link net.minecraft.server.PlayerManager#respawnPlayer(ServerPlayerEntity, boolean)}
     */
    private void teleportToRespawnPoint() {
        var player = ((ServerPlayerEntity) (Object) this);
        var respawnWorld = server.getWorld(getSpawnPointDimension());
        var respawnPos = getSpawnPointPosition();
        Optional<Vec3d> spawnablePos;
        if (respawnWorld != null && respawnPos != null) {
            spawnablePos = PlayerEntity.findRespawnPosition(respawnWorld, respawnPos, getSpawnAngle(), false, true);
        } else {
            spawnablePos = Optional.empty();
        }
        var targetWorld = respawnWorld != null && spawnablePos.isPresent() ? respawnWorld : server.getOverworld();
        if (spawnablePos.isPresent()) {
            var pos = spawnablePos.get();
            player.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), 0);
        } else {
            if (respawnPos != null) {
                var noBlock = GameStateChangeS2CPacket.NO_RESPAWN_BLOCK;
                var demo = GameStateChangeS2CPacket.DEMO_OPEN_SCREEN;
                networkHandler.sendPacket(new GameStateChangeS2CPacket(noBlock, demo));
            }
            var spawnPos = targetWorld.getSpawnPos();
            player.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        }

        while (!targetWorld.isSpaceEmpty(player) && player.getY() < targetWorld.getTopY()) {
            player.setPosition(player.getX(), player.getY() + 1, player.getZ());
        }
        player.teleport(respawnWorld, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
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
}
