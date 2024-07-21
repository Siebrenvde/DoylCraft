package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class GetOwnerCommand {

    public static void register(Commands commands, MemoryHandler memoryHandler) {

        commands.register(
            Commands.literal("getowner")
                .requires(source -> source.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();

                    if(!memoryHandler.getOwnerListContains(player)) {
                        memoryHandler.addGetOwnerPlayer(player);
                        memoryHandler.startGetOwnerCountdown(player);
                        player.sendMessage(Component.text("Right click a pet to get its owner.", Colours.GENERIC));
                    } else {
                        memoryHandler.removeGetOwnerPlayer(player);
                        player.sendMessage(Component.text("Disabled owner viewer.", Colours.GENERIC));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build(),
            "Get the owner of an entity"
        );

    }

}
