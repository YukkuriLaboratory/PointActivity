package net.yukulab.pointactivity.mixin;

import net.minecraft.network.ClientConnection;
import net.yukulab.pointactivity.mixin.extension.ModLoadedFlagHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientConnection.class)
public class MixinClientConnection implements ModLoadedFlagHolder {
    @SuppressWarnings("checkstyle:membername")
    private boolean pointactivity$isModLoaded;

    @Override
    public boolean isModLoaded() {
        return pointactivity$isModLoaded;
    }

    @Override
    public void onModLoaded() {
        pointactivity$isModLoaded = true;
    }
}
