package dev.siebrenvde.doylcraft.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class CommandBase {

    protected static boolean isPlayer(CommandSourceStack source) {
        return source.getSender() instanceof Player;
    }

    protected static int withPlayer(CommandContext<CommandSourceStack> ctx, PlayerCommand command) throws CommandSyntaxException {
        command.run((Player) ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }

    @FunctionalInterface
    protected interface PlayerCommand {
        void run(Player player) throws CommandSyntaxException;
    }

}