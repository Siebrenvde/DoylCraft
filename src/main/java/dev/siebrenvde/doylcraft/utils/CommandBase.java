package dev.siebrenvde.doylcraft.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class CommandBase {

    protected static Predicate<CommandSourceStack> isPlayer() {
        return source -> source.getSender() instanceof Player;
    }

    protected static List<Entity> resolveEntities(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return context.getArgument("entities", EntitySelectorArgumentResolver.class).resolve(context.getSource());
    }

    protected static Command<CommandSourceStack> withPlayer(PlayerCommand command) {
        return ctx -> {
            command.run(ctx, (Player) ctx.getSource().getSender());
            return Command.SINGLE_SUCCESS;
        };
    }

    @FunctionalInterface
    protected interface PlayerCommand {
        void run(CommandContext<CommandSourceStack> context, Player player) throws CommandSyntaxException;
    }

}
