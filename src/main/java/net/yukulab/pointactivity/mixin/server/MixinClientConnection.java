package net.yukulab.pointactivity.mixin.server;

import net.minecraft.network.ClientConnection;
import net.yukulab.pointactivity.mixin.extension.ModLoadedFlagHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements ModLoadedFlagHolder {
    private boolean isModLoaded;

    @Override
    public boolean pointactivity$isModLoaded() {
        return isModLoaded;
    }

    @Override
    public void pointactivity$onModLoaded() {
        isModLoaded = true;
    }
}
