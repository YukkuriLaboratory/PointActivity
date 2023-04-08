package net.yukulab.pointactivity.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.extension.PointHolder;
import net.yukulab.pointactivity.point.PointContainer;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements PointHolder {
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
}
