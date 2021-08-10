package dev.siebrenvde.doylcraft.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PvPCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        if(args.length == 1) {
            List<String> states = new ArrayList<>();
            states.add("on"); states.add("off");

            if(args[0].length() > 0) {
                List<String> nStates = new ArrayList<>();
                for(String state : states) {
                    if(state.startsWith(args[0])) {
                        nStates.add(state);
                    }
                }
                return nStates;
            }

            return states;
        }

        else if(args.length == 2) {
            List<String> worlds = new ArrayList<>();
            worlds.add("world"); worlds.add("world_nether"); worlds.add("world_the_end");

            if(args[1].length() > 0) {
                List<String> nWorlds = new ArrayList<>();
                for(String world : worlds) {
                    if(world.startsWith(args[1])) {
                        nWorlds.add(world);
                    }
                }
                return nWorlds;
            }

            return worlds;
        }

        return null;
    }

}
