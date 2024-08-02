package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.commands.arguments.OfflinePlayerArgumentType;
import dev.siebrenvde.doylcraft.handlers.LuckPermsHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Messages;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class GroupCommand {

    private final LuckPermsHandler luckPermsHandler;

    public GroupCommand(LuckPermsHandler luckPermsHandler) { this.luckPermsHandler = luckPermsHandler; }

    public void register(Commands commands) {

        commands.register(
            Commands.literal("group")
                .requires(source -> source.getSender().hasPermission("doylcraft.group"))
                .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                    .executes(this::getPlayerGroup)
                    .then(Commands.argument("group", StringArgumentType.word())
                        .suggests(this::getGroups)
                        .executes(this::setPlayerGroup)
                    )
                )
                .build(),
            "Change a player's group",
            List.of("rank")
        );

    }

    private int getPlayerGroup(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);

        try {
            luckPermsHandler.getPlayerGroup(player).thenAcceptAsync(group -> {

                if(group != null) {
                    sender.sendMessage(
                        Component.empty()
                            .append(Components.entityComponent(player).color(Colours.DATA))
                            .append(Component.text(" is a member of ", Colours.GENERIC))
                            .append(Component.text(group, Colours.DATA))
                            .append(Component.text(".", Colours.GENERIC))
                    );
                } else {
                    sender.sendMessage(
                        Component.empty()
                            .append(Components.entityComponent(player).color(Colours.DATA))
                            .append(Component.text(" is not a member of any group.", Colours.GENERIC))
                    );
                }

            });
        } catch(Exception exception) {
            sender.sendMessage(Messages.error(
                Component.text("Failed to get ", Colours.ERROR)
                    .append(Component.text(player.getName(), Colours.DATA))
                    .append(Component.text("'s group.", Colours.ERROR)), exception
            ));
            exception.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    private int setPlayerGroup(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        String group = context.getArgument("group", String.class);

        if(!luckPermsHandler.groupExists(group)) {
            sender.sendMessage(
                Component.text("Group ", Colours.ERROR)
                    .append(Component.text(group, Colours.DATA))
                    .append(Component.text(" does not exist.", Colours.ERROR))
            );
        }

        try {
            luckPermsHandler.setPlayerGroup(player, group);
            sender.sendMessage(
                Component.text("Changed ", Colours.GENERIC)
                    .append(Components.entityComponent(player).color(Colours.DATA))
                    .append(Component.text("'s group to ", Colours.GENERIC))
                    .append(Component.text(group, Colours.DATA))
                    .append(Component.text(".", Colours.GENERIC))
            );
        } catch(Exception exception) {
            sender.sendMessage(Messages.error(
                Component.text("Failed to change ", Colours.ERROR)
                    .append(Component.text(player.getName(), Colours.DATA))
                    .append(Component.text("'s group.", Colours.ERROR)), exception
            ));
            exception.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    private CompletableFuture<Suggestions> getGroups(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        luckPermsHandler.getGroups().stream().map(Group::getName).filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

}
