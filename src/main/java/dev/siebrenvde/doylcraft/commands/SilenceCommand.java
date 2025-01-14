package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static dev.siebrenvde.doylcraft.handlers.MemoryHandler.SILENCE_PLAYERS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

@SuppressWarnings("UnstableApiUsage")
public class SilenceCommand {

    public static void register(Commands commands) {
        commands.register(
            literal("silence")
                .requires(source -> source.getSender() instanceof Player)
                .then(literal("query")
                    .executes(ctx -> {
                        Player player = (Player) ctx.getSource().getSender();

                        if(!SILENCE_PLAYERS.containsKey(player)) {
                            SILENCE_PLAYERS.put(player, CommandType.QUERY);
                            MemoryHandler.startSilenceCountdown(player);
                            player.sendMessage(Component.text("Right click a animal to see whether it's silenced", Colours.GENERIC));
                        } else {
                            SILENCE_PLAYERS.remove(player);
                            player.sendMessage(Component.text("Disabled animal silencer", Colours.GENERIC));
                        }

                        return Command.SINGLE_SUCCESS;
                    })
                    .then(argument("entities", ArgumentTypes.entities())
                        .requires(source -> source.getSender().hasPermission("doylcraft.command.silence.query.selector"))
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();
                            ctx.getArgument("entities", EntitySelectorArgumentResolver.class)
                                .resolve(ctx.getSource()).forEach(entity -> query(player, entity));
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
                .then(literal("set")
                    .then(argument("state", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();
                            boolean toSilence = BoolArgumentType.getBool(ctx, "state");

                            if(!SILENCE_PLAYERS.containsKey(player)) {
                                SILENCE_PLAYERS.put(
                                    player,
                                    toSilence ? CommandType.SET_TRUE : CommandType.SET_FALSE
                                );
                                MemoryHandler.startSilenceCountdown(player);
                                player.sendMessage(Component.text(
                                    String.format("Right click a animal to %s it", toSilence ? "silence": "unsilence"),
                                    Colours.GENERIC
                                ));
                            } else {
                                SILENCE_PLAYERS.remove(player);
                                player.sendMessage(Component.text("Disabled animal silencer", Colours.GENERIC));
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(argument("entities", ArgumentTypes.entities())
                            .requires(source -> source.getSender().hasPermission("doylcraft.command.silence.set.selector"))
                            .executes(ctx -> {
                                Player player = (Player) ctx.getSource().getSender();
                                ctx.getArgument("entities", EntitySelectorArgumentResolver.class)
                                    .resolve(ctx.getSource()).forEach(entity -> {
                                        set(player, entity, BoolArgumentType.getBool(ctx, "state"));
                                    });
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
                .build(),
            "Silence an animal"
        );
    }

    public static void query(Player player, Entity entity) {
        player.sendMessage(
            Component.text()
                .append(Components.entity(entity).color(Colours.DATA))
                .append(Component.text(" is "))
                .append(entity.isSilent()
                    ? Component.text("silenced", Colours.POSITIVE)
                    : Component.text("not silenced", Colours.NEGATIVE)
                )
        );
    }

    public static void set(Player player, Entity entity, boolean toSilence) {
        if(!(entity instanceof Animals animal)) {
            player.sendMessage(
                Component.text()
                    .append(Components.entity(entity).color(Colours.DATA))
                    .append(Component.text(" cannot be silenced", Colours.GENERIC))
            );
            return;
        }
        player.sendMessage(
            Component.text()
                .append(Components.entity(entity).color(Colours.DATA))
                .append(Component.text(
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
