package net.yukulab.pointactivity;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.yukulab.pointactivity.network.Networking;

@Environment(EnvType.SERVER)
public class PointActivityServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Networking.registerServerReceivers();
    }
}
