package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.commands.arguments.OfflinePlayerArgumentType;
import dev.siebrenvde.doylcraft.addons.LuckPermsAddon;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.model.group.Group;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.siebrenvde.doylcraft.utils.Components.entity;
import static net.kyori.adventure.text.Component.text;

/**
 * Command to get or set a player's group
 */
@SuppressWarnings({"UnstableApiUsage", "CallToPrintStackTrace"})
@NullMarked
public class GroupCommand extends CommandBase {

    public static void register(Commands commands) {

        commands.register(
            Commands.literal("group")
                .requires(hasPermission("doylcraft.command.group"))
                .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                    .executes(GroupCommand::getPlayerGroup)
                    .then(Commands.argument("group", StringArgumentType.word())
                        .suggests(GroupCommand::getGroups)
                        .executes(GroupCommand::setPlayerGroup)
                    )
                )
                .build(),
            "Change a player's group",
            List.of("rank")
        );

    }

    private static int getPlayerGroup(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);

        try {
            LuckPermsAddon.get().getPlayerGroup(player).thenAcceptAsync(group -> {

                if(group != null) {
                    sender.sendMessage(
                        text()
                            .append(entity(player).color(Colours.DATA))
                            .append(text(" is a member of ", Colours.GENERIC))
                            .append(text(group, Colours.DATA))
                    );
                } else {
                    sender.sendMessage(
                        text()
                            .append(entity(player).color(Colours.DATA))
                            .append(text(" is not a member of any group", Colours.GENERIC))
                    );
                }

            });
        } catch(Exception exception) {
            sender.sendMessage(Components.exception(
                text()
                    .append(text("Failed to get "))
                    .append(entity(player).color(Colours.DATA))
                    .append(text("'s group"))
                    .color(NamedTextColor.RED)
                    .build(),
                exception
            ));
            exception.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int setPlayerGroup(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        String group = context.getArgument("group", String.class);

        if(!LuckPermsAddon.get().groupExists(group)) {
            sender.sendMessage(
                text()
                    .append(text("Group "))
                    .append(text(group, Colours.DATA))
                    .append(text(" does not exist"))
                    .color(NamedTextColor.RED)
            );
        }

        try {
            LuckPermsAddon.get().setPlayerGroup(player, group);
            sender.sendMessage(
                text()
                    .append(text("Changed "))
                    .append(entity(player).color(Colours.DATA))
                    .append(text("'s group to "))
                    .append(text(group, Colours.DATA))
                    .color(Colours.GENERIC)
            );
        } catch(Exception exception) {
            sender.sendMessage(Components.exception(
                text()
                    .append(text("Failed to change "))
                    .append(entity(player).color(Colours.DATA))
                    .append(text("'s group"))
                    .color(NamedTextColor.RED)
                    .build(),
                exception
            ));
            exception.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getGroups(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        LuckPermsAddon.get().getGroups().stream()
            .map(Group::getName)
            .filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

}
