package net.yukulab.pointactivity;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.yukulab.pointactivity.point.PointContainer;

import java.util.Optional;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    protected Commands() {
        throw new UnsupportedOperationException("Do not create this class instance");
    }

    @SuppressWarnings("checkstyle:LineLength")
    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("pa")
                        .requires((serverCommandSource ->
                                serverCommandSource.getEntity() instanceof ServerPlayerEntity player
                                        && player.pointactivity$getPointContainer().isPresent())
                        )
                        .then(literal("point")
                                .then(literal("set")
                                        .then(argument("point", integer(0))
                                                .suggests((context, builder) -> {
                                                    if (context.getSource().getEntity() instanceof ServerPlayerEntity player) {
                                                        player.pointactivity$getPointContainer()
                                                                .ifPresent(pointContainer ->
                                                                        builder.suggest(pointContainer.getPoint())
                                                                );
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes((context -> {
                                                    var newPoint = context.getArgument("point", Integer.class);
                                                    var source = context.getSource();
                                                    Optional.ofNullable(((ServerPlayerEntity) source.getEntity()))
                                                            .flatMap(ServerPlayerEntity::pointactivity$getPointContainer)
                                                            .ifPresentOrElse(
                                                                    container -> {
                                                                        var rev = container.getPoint();
                                                                        container.setPoint(newPoint);
                                                                        source.sendMessage(Text.literal("ポイントを適用しました ").append(String.format("%d->%d", rev, newPoint)));
                                                                    },
                                                                    () -> source.sendError(Text.literal("ポイントの適用に失敗しました"))
                                                            );
                                                    return 1;
                                                }))
                                        )
                                ).then(literal("get")
                                        .executes(context -> {
                                            var source = context.getSource();
                                            Optional.ofNullable(((ServerPlayerEntity) source.getEntity()))
                                                    .flatMap(ServerPlayerEntity::pointactivity$getPointContainer)
                                                    .ifPresentOrElse(
                                                            container -> source.sendMessage(Text.literal("ポイント: " + container.getPoint())),
                                                            () -> source.sendError(Text.literal("情報の取得に失敗しました"))
                                                    );
                                            return 1;
                                        }))
                        ).then(literal("sync")
                                .then(argument("target", player()).executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    var player = source.getPlayer();
                                    if (player == null) {
                                        source.sendError(Text.of("ゲーム内でのみ有効なコマンドです"));
                                        return 0;
                                    }
                                    if (!player.isSpectator()) {
                                        source.sendError(Text.of("spectatorモードでのみ有効です"));
                                        return 0;
                                    }
                                    if (player.pointactivity$getPointContainer().map(PointContainer::isShadowMode).orElse(false)) {
                                        source.sendError(Text.of("既に有効になっています"));
                                        return 0;
                                    }
                                    var target = getPlayer(context, "target");
                                    if (target == null) {
                                        source.sendError(Text.of("対象が存在しません"));
                                        return 0;
                                    }
                                    if (player == target) {
                                        source.sendError(Text.of("自身を選択することは出来ません"));
                                        return 0;
                                    }
                                    Optional<PointContainer> pointContainer = target.pointactivity$getPointContainer();
                                    if (pointContainer.isEmpty()) {
                                        source.sendError(Text.of("対象のプレイヤーがModを正しく導入していることを確認してください"));
                                        return 0;
                                    }
                                    pointContainer.ifPresent(container -> container.addShadowedPlayer(player));
                                    source.sendMessage(Text.of(String.format("%sのポイントと同期しました", target.getGameProfile().getName())));
                                    return Command.SINGLE_SUCCESS;
                                })))
                )
        ));
    }
}
