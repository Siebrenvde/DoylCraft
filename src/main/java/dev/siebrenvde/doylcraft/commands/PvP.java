package dev.siebrenvde.doylcraft.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.WorldGuardHandler;
import dev.siebrenvde.doylcraft.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PvP implements CommandExecutor {

    private Main main;
    private WorldGuardHandler handler;

    private List<String> worlds = new ArrayList<>();

    public PvP(Main m) {
        main = m;
        handler = m.getWorldGuardHandler();
        worlds.add("world");
        worlds.add("world_nether");
        worlds.add("world_the_end");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!sender.hasPermission("doylcraft.pvp.toggle")) {
            sender.sendMessage(Messages.permissionMessage);
            return false;
        }

        if(args.length == 0) {
            List<String> enabled = new ArrayList<>();
            List<String> disabled = new ArrayList<>();

            for(String world : worlds) {
                if(getState(world)) {
                    enabled.add(world);
                } else {
                    disabled.add(world);
                }
            }

            if(enabled.size() == 3 && disabled.size() == 0) {
                sender.sendMessage(ChatColor.GRAY + "PvP is " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + " in all worlds.");
                return true;
            } else if(enabled.size() > 0) {
                sender.sendMessage(ChatColor.GRAY + "PvP is " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + " in " + String.join(", ", enabled) + ".");
            }

            if(disabled.size() == 3 && enabled.size() == 0) {
                sender.sendMessage(ChatColor.GRAY + "PvP is " + ChatColor.RED + "disabled" + ChatColor.GRAY + " in all worlds.");
                return true;
            } else if(disabled.size() > 0) {
                sender.sendMessage(ChatColor.GRAY + "PvP is " + ChatColor.RED + "disabled" + ChatColor.GRAY + " in " + String.join(", ", disabled) + ".");
            }

            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("on")) {
                setAllStates(true);
                sender.sendMessage(ChatColor.GREEN + "Enabled " + ChatColor.GRAY + "PvP in all worlds.");
                return true;
            } else if(args[0].equalsIgnoreCase("off")) {
                setAllStates(false);
                sender.sendMessage(ChatColor.RED + "Disabled " + ChatColor.GRAY + "PvP in all worlds.");
                return true;
            } else {
                sender.sendMessage(Messages.usageMessage(Messages.CommandUsage.PVP));
                return false;
            }
        }

        if(args.length == 2) {
            if(!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
                sender.sendMessage(Messages.usageMessage(Messages.CommandUsage.PVP));
                return false;
            }
            String world = args[1].toLowerCase();
            if(!worlds.contains(world)) {
                sender.sendMessage(ChatColor.RED + "Unknown world \"" + world + "\".\n" + Messages.usageMessage("/pvp [on/off] [<world>]"));
                return false;
            }

            if(args[0].equalsIgnoreCase("on")) {
                setState(world, true);
                sender.sendMessage(ChatColor.GREEN + "Enabled " + ChatColor.GRAY + "PvP in " + world + ".");
            } else if(args[0].equalsIgnoreCase("off")) {
                setState(world, false);
                sender.sendMessage(ChatColor.RED + "Disabled " + ChatColor.GRAY + "PvP in " + world + ".");
            }
            return true;
        }

        else {
            sender.sendMessage(Messages.usageMessage(Messages.CommandUsage.PVP));
            return false;
        }

    }

    private void logInfo(String message) { main.getLogger().info(message); }

    private void setState(String world, boolean state) {
        ProtectedRegion region = handler.getRegion(world, "__global__");
        if(state) {
            region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
            logInfo("Set PvP to ALLOW in " + world);
        } else {
            region.setFlag(Flags.PVP, StateFlag.State.DENY);
            logInfo("Set PvP to DENY in " + world);
        }
    }

    private void setAllStates(boolean state) {
        for(String world : worlds) {
            setState(world, state);
        }
    }

    private boolean getState(String world) {
        ProtectedRegion region = handler.getRegion(world, "__global__");
        StateFlag.State state = region.getFlag(Flags.PVP);
        if(state == StateFlag.State.ALLOW || state == null) {
            return true;
        } else {
            return false;
        }
    }

}
