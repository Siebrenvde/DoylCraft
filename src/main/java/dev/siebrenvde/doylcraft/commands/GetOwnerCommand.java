package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.jspecify.annotations.NullMarked;

import static dev.siebrenvde.doylcraft.handlers.MemoryHandler.GET_OWNER_PLAYERS;

/**
 * Command to get the owner of a clicked entity
 */
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class GetOwnerCommand extends CommandBase {

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("getowner")
                .requires(isPlayer())
                .executes(withPlayer((ctx, player) -> {
                    if(!GET_OWNER_PLAYERS.contains(player)) {
                        GET_OWNER_PLAYERS.add(player);
                        MemoryHandler.startGetOwnerCountdown(player);
                        player.sendMessage(Component.text("Right click a pet to get its owner", Colours.GENERIC));
                    } else {
                        GET_OWNER_PLAYERS.remove(player);
                        player.sendMessage(Component.text("Disabled owner viewer", Colours.GENERIC));
                    }
                }))
                .then(Commands.argument("entities", ArgumentTypes.entities())
                    .requires(source -> source.getSender().hasPermission("doylcraft.command.getowner.selector"))
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

}
