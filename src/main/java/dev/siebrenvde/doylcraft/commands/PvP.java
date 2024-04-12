package dev.siebrenvde.doylcraft.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.WorldGuardHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PvP implements CommandExecutor {

    private Main main;
    private WorldGuardHandler worldGuardHandler;

    private final String USAGE = "/pvp [on/off] [<world>]";

    public PvP(Main main) {
        this.main = main;
        worldGuardHandler = main.getWorldGuardHandler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!sender.hasPermission("doylcraft.pvp.toggle")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(args.length == 0) {
            List<String> enabled = new ArrayList<>();
            List<String> disabled = new ArrayList<>();

            for(String world : getWorlds()) {
                if(getState(world)) {
                    enabled.add(world);
                } else {
                    disabled.add(world);
                }
            }

            if(enabled.size() == getWorlds().size() && disabled.isEmpty()) {
                sender.sendMessage(
                    Component.text("PvP is ", Colours.GENERIC)
                    .append(Component.text("enabled", Colours.POSITIVE))
                    .append(Component.text(" in ", Colours.GENERIC))
                    .append(Component.text("all worlds", Colours.GENERIC)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(String.join("\n", getWorlds())))))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return true;
            } else if(!enabled.isEmpty()) {
                sender.sendMessage(
                    Component.text("PvP is ", Colours.GENERIC)
                    .append(Component.text("enabled", Colours.POSITIVE))
                    .append(Component.text(" in " + String.join(", ", enabled) + ".", Colours.GENERIC))
                );
            }

            if(disabled.size() == getWorlds().size() && enabled.isEmpty()) {
                sender.sendMessage(
                    Component.text("PvP is ", Colours.GENERIC)
                    .append(Component.text("disabled", Colours.NEGATIVE))
                    .append(Component.text(" in ", Colours.GENERIC))
                    .append(Component.text("all worlds", Colours.GENERIC)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(String.join("\n", getWorlds())))))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return true;
            } else if(!disabled.isEmpty()) {
                sender.sendMessage(
                    Component.text("PvP is ", Colours.GENERIC)
                    .append(Component.text("disabled", Colours.NEGATIVE))
                    .append(Component.text(" in " + String.join(", ", disabled) + ".", Colours.GENERIC))
                );
            }

            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("on")) {
                setAllStates(true);
                sender.sendMessage(
                    Component.text("Enabled ", Colours.POSITIVE)
                    .append(Component.text(" in ", Colours.GENERIC))
                    .append(Component.text("all worlds", Colours.GENERIC)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(String.join("\n", getWorlds())))))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return true;
            } else if(args[0].equalsIgnoreCase("off")) {
                setAllStates(false);
                sender.sendMessage(
                    Component.text("Disabled ", Colours.NEGATIVE)
                    .append(Component.text(" in ", Colours.GENERIC))
                    .append(Component.text("all worlds", Colours.GENERIC)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(String.join("\n", getWorlds())))))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return true;
            } else if(args[0].equalsIgnoreCase("createglobalregions")) {
                try{
                    worldGuardHandler.createGlobalRegions();
                    sender.sendMessage(
                        Component.text("Created global regions.", Colours.GENERIC)
                    );
                    return true;
                } catch (Exception e){
                    sender.sendMessage(Messages.error("Failed to create global regions.", e));
                    e.printStackTrace();
                    return false;
                }
            } else {
                sender.sendMessage(Messages.usage(USAGE));
                return false;
            }
        }

        if(args.length == 2) {

            String world = args[1].toLowerCase();
            if(!getWorlds().contains(world) && !world.equals("fakeworld")) {
                sender.sendMessage(
                    Component.text("World ", Colours.ERROR)
                    .append(Component.text(world, Colours.DATA))
                    .append(Component.text(" does not exist.", Colours.ERROR))
                );
                return false;
            }

            if(args[0].equalsIgnoreCase("on")) {
                try {
                    setState(world, true);
                    sender.sendMessage(
                        Component.text("Enabled", Colours.POSITIVE)
                        .append(Component.text(" PvP in " + world + ".", Colours.GENERIC))
                    );
                    return true;
                } catch(Exception exception) {
                    sender.sendMessage(Messages.error(
                        Component.text("Failed to enable PvP in ", Colours.ERROR)
                        .append(Component.text(world, Colours.DATA))
                        .append(Component.text(".", Colours.ERROR)), exception
                    ));
                    exception.printStackTrace();
                    return false;
                }
            } else if(args[0].equalsIgnoreCase("off")) {
                try {
                    setState(world, false);
                    sender.sendMessage(
                        Component.text("Disabled", Colours.NEGATIVE)
                        .append(Component.text(" PvP in " + world + ".", Colours.GENERIC))
                    );
                    return true;
                } catch(Exception exception) {
                    sender.sendMessage(Messages.error(
                        Component.text("Failed to enable PvP in ", Colours.ERROR)
                        .append(Component.text(world, Colours.DATA))
                        .append(Component.text(".", Colours.ERROR)), exception
                    ));
                    exception.printStackTrace();
                    return false;
                }
            } else {
                sender.sendMessage(Messages.usage(USAGE));
                return false;
            }
        }

        else {
            sender.sendMessage(Messages.usage(USAGE));
            return false;
        }

    }

    private void logInfo(String message) { main.getLogger().info(message); }

    private void setState(String world, boolean state) {
        ProtectedRegion region = worldGuardHandler.getRegion(world, "__global__");
        if(state) {
            region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
            logInfo("Set PvP to ALLOW in " + world);
        } else {
            region.setFlag(Flags.PVP, StateFlag.State.DENY);
            logInfo("Set PvP to DENY in " + world);
        }
    }

    private void setAllStates(boolean state) {
        for(String world : getWorlds()) {
            setState(world, state);
        }
    }

    private boolean getState(String world) {
        ProtectedRegion region = worldGuardHandler.getRegion(world, "__global__");
        StateFlag.State state = region.getFlag(Flags.PVP);
        if(state == StateFlag.State.ALLOW || state == null) {
            return true;
        } else {
            return false;
        }
    }

    private List<String> getWorlds() {
        List<String> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            worlds.add(world.getName());
        }
        return worlds;
    }

}
