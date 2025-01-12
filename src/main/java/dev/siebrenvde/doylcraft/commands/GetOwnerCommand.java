package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static dev.siebrenvde.doylcraft.handlers.MemoryHandler.GET_OWNER_PLAYERS;

/**
 * Command to get the owner of a clicked entity
 */
@SuppressWarnings("UnstableApiUsage")
public class GetOwnerCommand {

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("getowner")
                .requires(source -> source.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();

                    if(!GET_OWNER_PLAYERS.contains(player)) {
                        GET_OWNER_PLAYERS.add(player);
                        MemoryHandler.startGetOwnerCountdown(player);
                        player.sendMessage(Component.text("Right click a pet to get its owner", Colours.GENERIC));
                    } else {
                        GET_OWNER_PLAYERS.remove(player);
                        player.sendMessage(Component.text("Disabled owner viewer", Colours.GENERIC));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build(),
            "Get the owner of an entity"
        );
    }

}
