package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetOwner implements CommandExecutor {

    private Main main;

    public GetOwner(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player player) {

            if(!main.listContains(player)) {
                main.addListPlayer(player);
                Utils.removeListCountdown(main, player);
                player.sendMessage(ChatColor.GREEN + "Right click a pet to get its owner.");
            } else {
                main.removeListPlayer(player);
                player.sendMessage(ChatColor.RED + "Disabled owner viewer.");
            }

            return true;

        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return false;
        }

    }

}
