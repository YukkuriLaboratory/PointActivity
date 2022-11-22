package net.yukulab.pointactivity.mixin.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.PointActivity;
import net.yukulab.pointactivity.mixin.extension.PointHolder;
import net.yukulab.pointactivity.point.PointContainer;
import net.yukulab.pointactivity.point.ServerPointContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements PointHolder {
    @SuppressWarnings("checkstyle:ConstantName")
    private static final String pointactivity$POINT_TAG = String.format("%s$pointcontainer", PointActivity.MOD_NAME);

    @SuppressWarnings("checkstyle:MemberName")
    private ServerPointContainer pointactivity$pointContainer;

    @Override
    public Optional<PointContainer> getPointContainer() {
        return Optional.ofNullable(pointactivity$pointContainer);
    }

    @Override
    public void initPointContainer() {
        if (getPointContainer().isPresent()) {
            throw new IllegalStateException("PointContainer already initialized!");
        }
        var player = (ServerPlayerEntity) (Object) this;
        pointactivity$pointContainer = new ServerPointContainer(player);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("RETURN")
    )
    public void writePointContainerData(NbtCompound nbt, CallbackInfo ci) {
        getPointContainer().ifPresent(container -> nbt.putInt(pointactivity$POINT_TAG, container.getPoint()));
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("RETURN")
    )
    public void readPointContainerData(NbtCompound nbt, CallbackInfo ci) {
        getPointContainer().ifPresent(container -> {
            var actionPoint = nbt.getInt(pointactivity$POINT_TAG);
            container.setPoint(actionPoint);
        });
    }
}
