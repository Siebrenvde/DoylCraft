package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

import static dev.siebrenvde.doylcraft.handlers.MemoryHandler.SILENCE_PLAYERS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
public class SilenceCommand extends CommandBase {

    public static void register(Commands commands) {
        commands.register(
            literal("silence")
                .requires(CommandBase::isPlayer)
                .then(literal("query")
                    .executes(ctx -> withPlayer(ctx, player -> {
                        if(!SILENCE_PLAYERS.containsKey(player)) {
                            SILENCE_PLAYERS.put(player, CommandType.QUERY);
                            MemoryHandler.startSilenceCountdown(player);
                            player.sendMessage(text("Right click a animal to see whether it's silenced", Colours.GENERIC));
                        } else {
                            SILENCE_PLAYERS.remove(player);
                            player.sendMessage(text("Disabled animal silencer", Colours.GENERIC));
                        }
                    }))
                    .then(argument("entities", ArgumentTypes.entities())
                        .requires(source -> hasPermission(source, "query.selector"))
                        .executes(ctx -> withPlayer(ctx, player -> {
                            resolveEntities(ctx).stream()
                                .filter(entity -> entity instanceof Animals)
                                .forEach(entity -> query(player, entity));
                        }))
                        .then(literal("highlight")
                            .then(argument("duration", ArgumentTypes.time())
                                .executes(ctx -> {
                                    // Gets all silent selected animals
                                    List<Entity> entities = resolveEntities(ctx).stream()
                                        .filter(entity -> entity instanceof Animals)
                                        .filter(Entity::isSilent)
                                        .toList();

                                    // Make entities glow
                                    entities.forEach(entity -> entity.setGlowing(true));

                                    int duration = ctx.getArgument("duration", Integer.class);
                                    ctx.getSource().getSender().sendMessage(
                                        text()
                                            .append(text("Highlighted "))
                                            .append(text(entities.size(), Colours.DATA))
                                            .append(text(" silenced animals for "))
                                            .append(duration >= 20
                                                ? Components.duration(Tick.of(duration)).color(Colours.DATA)
                                                : text(duration + " tick" + (duration != 1 ? "s" : ""), Colours.DATA)
                                            )
                                            .colorIfAbsent(Colours.GENERIC)
                                    );

                                    // Remove glow after duration
                                    if(!entities.isEmpty()) {
                                        Bukkit.getScheduler().runTaskLater(
                                            DoylCraft.getInstance(),
                                            () -> entities.forEach(entity -> entity.setGlowing(false)),
                                            duration
                                        );
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                            )
                        )
                    )
                )
                .then(literal("set")
                    .then(argument("state", BoolArgumentType.bool())
                        .executes(ctx -> withPlayer(ctx, player -> {
                            boolean toSilence = BoolArgumentType.getBool(ctx, "state");
                            if(!SILENCE_PLAYERS.containsKey(player)) {
                                SILENCE_PLAYERS.put(
                                    player,
                                    toSilence ? CommandType.SET_TRUE : CommandType.SET_FALSE
                                );
                                MemoryHandler.startSilenceCountdown(player);
                                player.sendMessage(text(
                                    String.format("Right click a animal to %s it", toSilence ? "silence": "unsilence"),
                                    Colours.GENERIC
                                ));
                            } else {
                                SILENCE_PLAYERS.remove(player);
                                player.sendMessage(text("Disabled animal silencer", Colours.GENERIC));
                            }
                        }))
                        .then(argument("entities", ArgumentTypes.entities())
                            .requires(source -> hasPermission(source, "set.selector"))
                            .executes(ctx -> withPlayer(ctx, player -> {
                                resolveEntities(ctx).stream()
                                    .filter(entity -> entity instanceof Animals)
                                    .forEach(entity -> {
                                        set(player, entity, BoolArgumentType.getBool(ctx, "state"));
                                    });
                            }))
                        )
                    )
                )
                .build(),
            "Silence an animal"
        );
    }

    private static boolean hasPermission(CommandSourceStack source, String permission) {
        return source.getSender().hasPermission("doylcraft.command.silence." + permission);
    }

    public static void query(Player player, Entity entity) {
        player.sendMessage(
            text()
                .append(Components.entity(entity).color(Colours.DATA))
                .append(text(" is "))
                .append(entity.isSilent()
                    ? text("silenced", Colours.POSITIVE)
                    : text("not silenced", Colours.NEGATIVE)
                )
        );
    }

    public static void set(Player player, Entity entity, boolean toSilence) {
        if(!(entity instanceof Animals animal)) {
            player.sendMessage(
                text()
                    .append(Components.entity(entity).color(Colours.DATA))
                    .append(text(" cannot be silenced", Colours.GENERIC))
            );
            return;
        }
        player.sendMessage(
            text()
                .append(Components.entity(entity).color(Colours.DATA))
                .append(text(
                    String.format(" is %s silenced", toSilence ? "now" : "no longer"),
                    Colours.GENERIC
                ))
        );
        animal.setSilent(toSilence);
    }

    public enum CommandType {
        QUERY,
        SET_TRUE,
        SET_FALSE,
    }

}
