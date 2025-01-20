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
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Command to get or set a player's group
 */
@SuppressWarnings("UnstableApiUsage")
public class GroupCommand extends CommandBase {

    private final LuckPermsAddon luckPermsAddon;

    public GroupCommand(LuckPermsAddon luckPermsAddon) { this.luckPermsAddon = luckPermsAddon; }

    public void register(Commands commands) {

        commands.register(
            Commands.literal("group")
                .requires(hasPermission("doylcraft.command.group"))
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
            luckPermsAddon.getPlayerGroup(player).thenAcceptAsync(group -> {

                if(group != null) {
                    sender.sendMessage(
                        Component.empty()
                            .append(Components.entity(player).color(Colours.DATA))
                            .append(Component.text(" is a member of ", Colours.GENERIC))
                            .append(Component.text(group, Colours.DATA))
                    );
                } else {
                    sender.sendMessage(
                        Component.empty()
                            .append(Components.entity(player).color(Colours.DATA))
                            .append(Component.text(" is not a member of any group", Colours.GENERIC))
                    );
                }

            });
        } catch(Exception exception) {
            sender.sendMessage(Components.exception(
                Component.text("Failed to get ", Colours.ERROR)
                    .append(Component.text(player.getName(), Colours.DATA))
                    .append(Component.text("'s group", Colours.ERROR)), exception
            ));
            exception.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    private int setPlayerGroup(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        String group = context.getArgument("group", String.class);

        if(!luckPermsAddon.groupExists(group)) {
            sender.sendMessage(
                Component.text("Group ", Colours.ERROR)
                    .append(Component.text(group, Colours.DATA))
                    .append(Component.text(" does not exist", Colours.ERROR))
            );
        }

        try {
            luckPermsAddon.setPlayerGroup(player, group);
            sender.sendMessage(
                Component.text("Changed ", Colours.GENERIC)
                    .append(Components.entity(player).color(Colours.DATA))
                    .append(Component.text("'s group to ", Colours.GENERIC))
                    .append(Component.text(group, Colours.DATA))
            );
        } catch(Exception exception) {
            sender.sendMessage(Components.exception(
                Component.text("Failed to change ", Colours.ERROR)
                    .append(Component.text(player.getName(), Colours.DATA))
                    .append(Component.text("'s group", Colours.ERROR)), exception
            ));
            exception.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    private CompletableFuture<Suggestions> getGroups(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        luckPermsAddon.getGroups().stream().map(Group::getName).filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

}
