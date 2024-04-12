package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Messages;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetOwnerCommand implements CommandExecutor {

    private Main main;

    public GetOwnerCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player player) {

            if(!main.listContains(player)) {
                main.addListPlayer(player);
                Utils.removeListCountdown(main, player);
                player.sendMessage(Component.text("Right click a pet to get its owner.", Colours.GENERIC));
            } else {
                main.removeListPlayer(player);
                player.sendMessage(Component.text("Disabled owner viewer.", Colours.GENERIC));
            }

            return true;

        } else {
            sender.sendMessage(Messages.PLAYER_ONLY);
            return false;
        }

    }

}
