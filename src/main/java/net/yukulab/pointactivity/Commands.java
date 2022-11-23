package net.yukulab.pointactivity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    protected Commands() {
        throw new UnsupportedOperationException("Do not create this class instance");
    }

    @SuppressWarnings("checkstyle:LineLength")
    @Environment(EnvType.SERVER)
    public static void registerForServer() {
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
                                                                        source.sendMessage(Text.literal("ポイントを適応しました ").append(String.format("%d->%d", rev, newPoint)));
                                                                    },
                                                                    () -> source.sendError(Text.literal("ポイントの適応に失敗しました"))
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
                        )
                )
        ));
    }
}
