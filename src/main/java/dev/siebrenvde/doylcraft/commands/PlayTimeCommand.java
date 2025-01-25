package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.commands.arguments.OfflinePlayerArgumentType;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.preferences.Preferences;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.*;

/**
 * Command to get the current player's or another player's current and total time played
 */
@SuppressWarnings("UnstableApiUsage")
public class PlayTimeCommand extends CommandBase {

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("playtime")
                .requires(isPlayer())
                .executes(withPlayer((ctx, player) -> {
                    ZoneId zone = Preferences.get(player).timezone();
                    player.sendMessage(
                        text()
                            .append(text("Current Online Time: "))
                            .append(onlineTime(player, zone).color(Colours.DATA))
                            .appendNewline()
                            .append(text("Total Time Played: "))
                            .append(totalTime(player, zone).color(Colours.DATA))
                            .color(Colours.GENERIC)
                    );
                }))
                .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);
                        ZoneId zone = sender instanceof Player
                            ? Preferences.get((Player) sender).timezone()
                            : ZoneId.from(ZoneOffset.UTC);

                        TextComponent.Builder message = text();

                        if(player.isOnline()) {
                            message
                                .append(Components.entity(player).color(Colours.GENERIC))
                                .append(text("'s Current Online Time: ", Colours.GENERIC))
                                .append(onlineTime(player.getPlayer(), zone).color(Colours.DATA))
                                .appendNewline();
                        }

                        message
                            .append(Components.entity(player).color(Colours.GENERIC))
                            .append(text("'s Total Time Played: ", Colours.GENERIC))
                            .append(totalTime(player, zone).color(Colours.DATA));

                        sender.sendMessage(message);

                        return Command.SINGLE_SUCCESS;
                    })
                )
                .build(),
            "Get your or another player's time played"
        );
    }

    private static Component onlineTime(Player player, ZoneId zone) {
        return Components.duration(Duration.between(MemoryHandler.LOGIN_TIMES.get(player), Instant.now()))
            .hoverEvent(HoverEvent.showText(
                text()
                    .append(text(String.format("Login Time (%s): ", zone.getId()), Colours.GENERIC))
                    .appendNewline()
                    .append(Components.timestamp(
                        MemoryHandler.LOGIN_TIMES.get(player),
                        zone
                    ).color(Colours.DATA))
            ));
    }

    private static Component totalTime(OfflinePlayer player, ZoneId zone) {
        return Components.duration(Tick.of(player.getStatistic(Statistic.PLAY_ONE_MINUTE)))
            .hoverEvent(HoverEvent.showText(
                text()
                    .append(text(String.format("First Login Time (%s): ", zone.getId()), Colours.GENERIC))
                    .appendNewline()
                    .append(Components.timestamp(
                        Instant.ofEpochMilli(player.getFirstPlayed()),
                        zone
                    ).color(Colours.DATA))
            ));
    }

}
