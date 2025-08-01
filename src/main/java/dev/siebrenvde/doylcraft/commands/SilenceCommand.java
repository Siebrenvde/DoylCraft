package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import dev.siebrenvde.doylcraft.utils.Permissions;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@NullMarked
public class SilenceCommand extends CommandBase {

    /**
     * The list of players who executed the command
     * and have not yet interacted with an entity
     */
    public static final Map<Player, CommandType> SILENCE_PLAYERS = new HashMap<>();

    public static void register(Commands commands) {
        commands.register(
            literal("silence")
                .requires(isPlayer())
                .then(literal("query")
                    .executes(withPlayer((ctx, player) -> {
                        if(!SILENCE_PLAYERS.containsKey(player)) {
                            SILENCE_PLAYERS.put(player, CommandType.QUERY);
                            startCountdown(player);
                            player.sendMessage(text("Right click a animal to see whether it's silenced", Colours.GENERIC));
                        } else {
                            SILENCE_PLAYERS.remove(player);
                            player.sendMessage(text("Disabled animal silencer", Colours.GENERIC));
                        }
                    }))
                    .then(argument("entities", ArgumentTypes.entities())
                        .requires(Permissions.COMMAND_SILENCE_QUERY_SELECTOR)
                        .then(argument("duration", ArgumentTypes.time())
                            .executes(withPlayer((ctx, player) -> {
                                // Gets all silent selected animals
                                List<Entity> entities = resolveEntities(ctx).stream()
                                    .filter(entity -> entity instanceof Animals)
                                    .filter(Entity::isSilent)
                                    .toList();

                                // Make entities glow
                                entities.forEach(entity -> entity.setGlowing(true));

                                int duration = ctx.getArgument("duration", Integer.class);
                                if(duration > 6000) {
                                    player.sendMessage(text("Duration cannot be longer than 5 minutes (6000 ticks)", NamedTextColor.RED));
                                    return;
                                }

                                player.sendMessage(
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
                                        DoylCraft.instance(),
                                        () -> entities.forEach(entity -> entity.setGlowing(false)),
                                        duration
                                    );
                                }
                            }))
                        )
                    )
                )
                .then(literal("set")
                    .then(argument("state", BoolArgumentType.bool())
                        .executes(withPlayer((ctx, player) -> {
                            boolean toSilence = BoolArgumentType.getBool(ctx, "state");
                            if(!SILENCE_PLAYERS.containsKey(player)) {
                                SILENCE_PLAYERS.put(
                                    player,
                                    toSilence ? CommandType.SET_TRUE : CommandType.SET_FALSE
                                );
                                startCountdown(player);
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
                            .requires(Permissions.COMMAND_SILENCE_SET_SELECTOR)
                            .executes(withPlayer((ctx, player) -> {
                                boolean toSilence = BoolArgumentType.getBool(ctx, "state");
                                List<Entity> entities = resolveEntities(ctx).stream()
                                    .filter(entity -> entity instanceof Animals)
                                    .filter(entity -> toSilence != entity.isSilent())
                                    .toList();

                                entities.forEach(entity -> {
                                    entity.setSilent(toSilence);
                                    entity.setGlowing(true);
                                });

                                player.sendMessage(
                                    text()
                                        .append(text(toSilence ? "Silenced " : "Unsilenced "))
                                        .append(text(entities.size(), Colours.DATA))
                                        .append(text(" animals"))
                                        .colorIfAbsent(Colours.GENERIC)
                                );

                                // Remove glow after duration
                                if(!entities.isEmpty()) {
                                    Bukkit.getScheduler().runTaskLater(
                                        DoylCraft.instance(),
                                        () -> entities.forEach(entity -> entity.setGlowing(false)),
                                        200 // 10 seconds
                                    );
                                }
                            }))
                        )
                    )
                )
                .build(),
            "Silence an animal"
        );
    }

    public static void query(Player player, Entity entity) {
        player.sendMessage(
            text()
                .append(Components.entity(entity).color(Colours.DATA))
                .append(text(" is "))
                .append(entity.isSilent()
                    ? text("silenced", NamedTextColor.GREEN)
                    : text("not silenced", NamedTextColor.RED)
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

    /**
     * Removes the player from {@link SilenceCommand#SILENCE_PLAYERS} after 10 seconds
     * @param player the player
     */
    private static void startCountdown(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(
            DoylCraft.instance(),
            () -> SILENCE_PLAYERS.remove(player),
            200L
        );
    }

}
