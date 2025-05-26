package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import dev.siebrenvde.doylcraft.utils.Permissions;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to get the owner of a clicked entity
 */
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class GetOwnerCommand extends CommandBase {

    /**
     * The list of players who executed the command
     * and have not yet interacted with an entity
     */
    public static final List<Player> GET_OWNER_PLAYERS = new ArrayList<>();

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("getowner")
                .requires(isPlayer())
                .executes(withPlayer((ctx, player) -> {
                    if(!GET_OWNER_PLAYERS.contains(player)) {
                        GET_OWNER_PLAYERS.add(player);
                        startCountdown(player);
                        player.sendMessage(Component.text("Right click a pet to get its owner", Colours.GENERIC));
                    } else {
                        GET_OWNER_PLAYERS.remove(player);
                        player.sendMessage(Component.text("Disabled owner viewer", Colours.GENERIC));
                    }
                }))
                .then(Commands.argument("entities", ArgumentTypes.entities())
                    .requires(Permissions.COMMAND_GETOWNER_SELECTOR)
                    .executes(withPlayer((ctx, player) -> {
                        resolveEntities(ctx).forEach(entity -> {
                            handle(player, entity);
                        });
                    }))
                )
                .build(),
            "Get the owner of an entity"
        );
    }

    public static void handle(Player player, Entity entity) {
        if(entity instanceof Tameable tameable) {
            if(tameable.isTamed() && tameable.getOwner() != null) {
                OfflinePlayer owner = (OfflinePlayer) tameable.getOwner();
                player.sendMessage(
                    Component.empty()
                        .append(Components.entity(entity).color(Colours.DATA))
                        .append(Component.text("'s owner is ", Colours.GENERIC))
                        .append(Components.entity(owner).color(Colours.DATA))
                );
                return;
            }
        }

        player.sendMessage(
            Component.empty()
                .append(Components.entity(entity).color(Colours.DATA))
                .append(Component.text(" doesn't have an owner", Colours.GENERIC))
        );
    }

    /**
     * Removes the player from {@link GetOwnerCommand#GET_OWNER_PLAYERS} after 10 seconds
     * @param player the player
     */
    private static void startCountdown(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(
            DoylCraft.instance(),
            () -> GET_OWNER_PLAYERS.remove(player),
            200L
        );
    }

}
