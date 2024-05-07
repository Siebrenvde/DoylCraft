package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetOwnerCommand implements CommandExecutor {

    private final MemoryHandler memoryHandler;

    public GetOwnerCommand(MemoryHandler memoryHandler) {
        this.memoryHandler = memoryHandler;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player player) {

            if(!memoryHandler.getOwnerListContains(player)) {
                memoryHandler.addGetOwnerPlayer(player);
                memoryHandler.startGetOwnerCountdown(player);
                player.sendMessage(Component.text("Right click a pet to get its owner.", Colours.GENERIC));
            } else {
                memoryHandler.removeGetOwnerPlayer(player);
                player.sendMessage(Component.text("Disabled owner viewer.", Colours.GENERIC));
            }

            return true;

        } else {
            sender.sendMessage(Messages.PLAYER_ONLY);
            return false;
        }

    }

}
