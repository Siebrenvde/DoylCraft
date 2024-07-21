package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.commands.arguments.OfflinePlayerArgumentType;
import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.*;

@SuppressWarnings("UnstableApiUsage")
public class PlayTimeCommand {

    public static void register(Commands commands, TimeHandler timeHandler) {

        commands.register(
            Commands.literal("playtime")
                .requires(source -> source.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();

                    player.sendMessage(
                        text("Current Online Time: ", Colours.GENERIC)
                            .append(text(TimeHandler.formatTime(timeHandler.getOnlineTime(player) / 1000), Colours.DATA))
                            .append(newline())
                            .append(text("Total Time Played: ", Colours.GENERIC))
                            .append(text(TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20), Colours.DATA))
                    );

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                    .suggests(PlayTimeCommand::getOnlinePlayers)
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);

                        TextComponent.Builder message = text();

                        if(player.isOnline()) {
                            message.append(Components.entityComponent(text(player.getName(), Colours.GENERIC), player))
                                .append(text("'s Current Online Time: ", Colours.GENERIC))
                                .append(text(TimeHandler.formatTime(timeHandler.getOnlineTime(player.getPlayer()) / 1000), Colours.DATA))
                                .append(Component.newline());
                        }

                        message.append(Components.entityComponent(text(player.getName(), Colours.GENERIC), player))
                            .append(text("'s Total Time Played: ", Colours.GENERIC))
                            .append(text(TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20), Colours.DATA));

                        sender.sendMessage(message.build());

                        return Command.SINGLE_SUCCESS;
                    })
                )
                .build(),
            "Get your or another player's time played"
        );

    }

    private static CompletableFuture<Suggestions> getOnlinePlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

}
