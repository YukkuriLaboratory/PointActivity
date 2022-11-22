package net.yukulab.pointactivity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.yukulab.pointactivity.network.Networking;

@Environment(EnvType.CLIENT)
public class PointActivityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Networking.registerClientReceivers();
    }
}
