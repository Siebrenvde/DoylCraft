package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.commands.arguments.OfflinePlayerArgumentType;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.*;

/**
 * Command to get the current player's or another player's current and total time played
 */
@SuppressWarnings("UnstableApiUsage")
public class PlayTimeCommand {

    private final MemoryHandler memoryHandler;

    public PlayTimeCommand(MemoryHandler memoryHandler) {
        this.memoryHandler = memoryHandler;
    }

    public void register(Commands commands) {

        commands.register(
            Commands.literal("playtime")
                .requires(source -> source.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();

                    player.sendMessage(
                        text("Current Online Time: ", Colours.GENERIC)
                            .append(onlineTime(player).color(Colours.DATA))
                            .append(newline())
                            .append(text("Total Time Played: ", Colours.GENERIC))
                            .append(totalTime(player).color(Colours.DATA))
                    );

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                    .suggests(this::getOnlinePlayers)
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);

                        TextComponent.Builder message = text();

                        if(player.isOnline()) {
                            message.append(Components.entity(player).color(Colours.GENERIC))
                                .append(text("'s Current Online Time: ", Colours.GENERIC))
                                    .append(onlineTime(player.getPlayer()).color(Colours.DATA))
                                .append(Component.newline());
                        }

                        message.append(Components.entity(player).color(Colours.GENERIC))
                            .append(text("'s Total Time Played: ", Colours.GENERIC))
                                .append(totalTime(player).color(Colours.DATA));

                        sender.sendMessage(message.build());

                        return Command.SINGLE_SUCCESS;
                    })
                )
                .build(),
            "Get your or another player's time played"
        );

    }

    private CompletableFuture<Suggestions> getOnlinePlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    private Component onlineTime(Player player) {
        return Components.elapsedTime(memoryHandler.getOnlineTime(player) / 1000)
                .hoverEvent(HoverEvent.showText(
                    text("Login Time (UTC): ", Colours.GENERIC)
                    .append(Components.timestamp(memoryHandler.getLoginTime(player)).color(Colours.DATA))
                ));
    }

    private Component totalTime(OfflinePlayer player) {
        return Components.elapsedTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20)
            .hoverEvent(HoverEvent.showText(
                    text("First Login Time (UTC): ", Colours.GENERIC)
                    .append(Components.timestamp(player.getFirstPlayed()).color(Colours.DATA))
            ));
    }

}
