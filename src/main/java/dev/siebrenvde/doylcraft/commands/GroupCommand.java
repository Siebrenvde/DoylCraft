package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.handlers.LuckPermsHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Messages;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GroupCommand implements CommandExecutor {

    private LuckPermsHandler luckPermsHandler;

    public GroupCommand(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!sender.hasPermission("doylcraft.group")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        else if(args.length == 1) {

            OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(args[0]);

            if(player == null) {
                sender.sendMessage(Messages.playerNotFound(args[0]));
                return false;
            }

            try {
                luckPermsHandler.getPlayerGroup(player).thenAcceptAsync(group -> {

                    if(group != null) {
                        sender.sendMessage(
                            Component.empty()
                            .append(Utils.entityComponent(Component.text(player.getName(), Colours.DATA), player))
                            .append(Component.text(" is a member of ", Colours.GENERIC))
                            .append(Component.text(group, Colours.DATA))
                            .append(Component.text(".", Colours.GENERIC))
                        );
                    } else {
                        sender.sendMessage(
                            Component.empty()
                            .append(Utils.entityComponent(Component.text(player.getName(), Colours.DATA), player))
                            .append(Component.text(" is not a member of any group.", Colours.GENERIC))
                        );
                    }

                });

                return true;
            } catch(Exception exception) {
                sender.sendMessage(Messages.error(
                    Component.text("Failed to get ", Colours.ERROR)
                    .append(Component.text(player.getName(), Colours.DATA))
                    .append(Component.text("'s group.", Colours.ERROR)), exception
                ));
                exception.printStackTrace();
                return false;
            }

        }

        else if(args.length == 2) {

            OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(args[0]);

            if(player == null) {
                sender.sendMessage(Messages.playerNotFound(args[0]));
                return false;
            }

            try {
                String group = args[1];
                if(!luckPermsHandler.groupExists(group)) {
                    sender.sendMessage(
                            Component.text("Group ", Colours.ERROR)
                                    .append(Component.text(group, Colours.DATA))
                                    .append(Component.text(" does not exist.", Colours.ERROR))
                    );
                    return false;
                }

                luckPermsHandler.setPlayerGroup(player, group);
                sender.sendMessage(
                    Component.text("Changed ", Colours.GENERIC)
                    .append(Utils.entityComponent(Component.text(player.getName(), Colours.DATA), player))
                    .append(Component.text("'s group to ", Colours.GENERIC))
                    .append(Component.text(group, Colours.DATA))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return true;
            } catch(Exception exception) {
                sender.sendMessage(Messages.error(
                    Component.text("Failed to change ", Colours.ERROR)
                    .append(Component.text(player.getName(), Colours.DATA))
                    .append(Component.text("'s group.", Colours.ERROR)), exception
                ));
                exception.printStackTrace();
                return false;
            }

        }

        else {
            sender.sendMessage(Messages.usage("/group <player> [<group>]"));
            return false;
        }
    }

}
