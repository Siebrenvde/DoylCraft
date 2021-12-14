package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import dev.siebrenvde.doylcraft.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTime implements CommandExecutor {

    private TimeHandler handler;

    public PlayTime(Main main) {
        handler = main.getTimeHandler();
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(args.length == 0) {
            if(sender instanceof Player player) {
                sender.sendMessage(ChatColor.YELLOW + "Current Online Time: " + ChatColor.GRAY + TimeHandler.formatTime(handler.getOnlineTime(player) / 1000));
                sender.sendMessage(ChatColor.YELLOW + "Total Time Played: " + ChatColor.GRAY + TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20));
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
                return false;
            }
        }

        else if(args.length == 1) {
            Player player = Bukkit.getPlayerExact(args[0]);
         if(player == null) {
             sender.sendMessage(ChatColor.GRAY + "Player " + ChatColor.RED + args[0] + ChatColor.GRAY + " does not exist or is offline.");
             return false;
         }
            sender.sendMessage(ChatColor.YELLOW + player.getName() + "Current Online Time: " + ChatColor.GRAY + TimeHandler.formatTime(handler.getOnlineTime(player) / 1000));
            sender.sendMessage(ChatColor.YELLOW + player.getName() + "Total Time Played: " + ChatColor.GRAY + TimeHandler.formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20));
            return true;
        }

        else {
            sender.sendMessage(Messages.usageMessage(Messages.CommandUsage.PLAYTIME));
            return false;
        }

    }

}
