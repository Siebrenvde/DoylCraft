package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.handlers.LuckPermsHandler;
import dev.siebrenvde.doylcraft.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Rank implements CommandExecutor {

    private LuckPermsHandler luckPermsHandler;

    public Rank(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!sender.hasPermission("doylcraft.rank")) {
            sender.sendMessage(Messages.permissionMessage);
            return false;
        }

        else if(args.length == 1) {

            OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(args[0]);

            if(player == null) {
                sender.sendMessage(ChatColor.GRAY + "Player " + ChatColor.RED + args[0] + ChatColor.GRAY + " does not exist or has not joined the server before.");
                return false;
            }

            luckPermsHandler.getPlayerGroup(player).thenAcceptAsync(group -> {

                if(group != null) {
                    sender.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " is a member of " + ChatColor.YELLOW + group + ChatColor.GRAY + ".");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " is not in any groups.");
                }

            });

            return true;
        }

        else if(args.length == 2) {

            OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(args[0]);

            if(player == null) {
                sender.sendMessage(ChatColor.GRAY + "Player " + ChatColor.RED + args[0] + ChatColor.GRAY + " does not exist or has not joined the server before.");
                return false;
            }

            String group = args[1];
            if(!luckPermsHandler.groupExists(group)) {
                sender.sendMessage(ChatColor.GRAY + "Group " + ChatColor.RED + group + ChatColor.GRAY + " does not exist.");
                return false;
            }

            luckPermsHandler.setPlayerGroup(player, group);
            sender.sendMessage(ChatColor.GRAY + "Changed " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + "'s group to " + ChatColor.GREEN + group + ChatColor.GRAY + ".");
            return true;
        }

        else {
            sender.sendMessage(Messages.usageMessage(Messages.CommandUsage.RANK));
            return false;
        }
    }

}
