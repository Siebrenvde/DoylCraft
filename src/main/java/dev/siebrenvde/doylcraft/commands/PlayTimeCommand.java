package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Messages;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand implements CommandExecutor {

    private TimeHandler timeHandler;

    public PlayTimeCommand(TimeHandler timeHandler) {
        this.timeHandler = timeHandler;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(args.length == 0) {
            if(sender instanceof Player player) {
                player.sendMessage(
                    Component.text("Current Online Time: ", Colours.GENERIC)
                    .append(Component.text(TimeHandler.formatTime(timeHandler.getOnlineTime(player) / 1000), Colours.DATA))
                    .append(Component.newline())
                    .append(Component.text("Total Time Played: ", Colours.GENERIC))
                    .append(Component.text(TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20), Colours.DATA))
                );
                return true;
            } else {
                sender.sendMessage(Messages.PLAYER_ONLY);
                return false;
            }
        }

        else if(args.length == 1) {

            OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(args[0]);

            if(player == null) {
                sender.sendMessage(Messages.playerNotFound(args[0]));
                return false;
            }

            if(player.isOnline()) {
                sender.sendMessage(
                    Component.empty()
                    .append(Utils.entityComponent(Component.text(player.getName(), Colours.GENERIC), player))
                    .append(Component.text("'s Current Online Time: ", Colours.GENERIC))
                    .append(Component.text(TimeHandler.formatTime(timeHandler.getOnlineTime(player.getPlayer()) / 1000), Colours.DATA))
                    .append(Component.newline())
                    .append(Utils.entityComponent(Component.text(player.getName(), Colours.GENERIC), player))
                    .append(Component.text("'s Total Time Played: ", Colours.GENERIC))
                    .append(Component.text(TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20), Colours.DATA))
                );

                return true;
            }

            sender.sendMessage(
                Component.empty()
                .append(Utils.entityComponent(Component.text(player.getName(), Colours.GENERIC), player))
                .append(Component.text("'s Total Time Played: ", Colours.GENERIC))
                .append(Component.text(TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20), Colours.DATA))
            );

            return true;
        }

        else {
            sender.sendMessage(Messages.usage("/playtime [<player>]"));
            return false;
        }

    }

}
