package net.yukulab.pointactivity.point;

import net.minecraft.server.network.ServerPlayerEntity;
import net.yukulab.pointactivity.network.packet.play.UpdatePointS2CPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerPointContainer extends PointContainer {
    private final List<UUID> shadowingPlayers = new ArrayList<>();
    private final ServerPlayerEntity player;

    public ServerPointContainer(ServerPlayerEntity playerEntity) {
        player = playerEntity;
    }

    public void subtractPoint(int amount, PointReason reason) {
        addReasonPoint(reason, amount);
        subtractPoint(amount);
    }

    @Override
    public void subtractPoint(int amount) {
        super.subtractPoint(amount);
        sendUpdatePacket();
    }

    @Override
    public void addShadowedPlayer(ServerPlayerEntity player) {
        shadowingPlayers.add(player.getUuid());
        player.pointactivity$getPointContainer().ifPresent(container -> container.isShadowMode = true);
        UpdatePointS2CPacket.send(player, currentPoint, reasonCache, true);
    }

    @Override
    public void removeShadowedPlayer(ServerPlayerEntity player) {
        shadowingPlayers.remove(player.getUuid());
    }

    @Override
    public void setPoint(int point) {
        super.setPoint(point);
        if (point != currentPoint) {
            currentPoint = point;
            if (player.networkHandler != null) {
                sendUpdatePacket();
            }
        }
    }

    private void sendUpdatePacket() {
        UpdatePointS2CPacket.send(player, currentPoint, reasonCache, isShadowMode());
        shadowingPlayers.forEach(playerId -> {
            var targetPlayer = player.server.getPlayerManager().getPlayer(playerId);
            if (targetPlayer != null) {
                UpdatePointS2CPacket.send(targetPlayer, currentPoint, reasonCache, true);
            }
        });
    }
}
