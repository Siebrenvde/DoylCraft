package dev.siebrenvde.doylcraft.tabcompleters;

import dev.siebrenvde.doylcraft.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
            return Utils.autoCompleter(args[0], states);
        }

        else if(args.length == 2) {
            List<String> worlds = new ArrayList<>();
            for(World bWorld : Bukkit.getWorlds()) {
                worlds.add(bWorld.getName());
            }
            return Utils.autoCompleter(args[1], worlds);
        }

        return null;
    }

}
