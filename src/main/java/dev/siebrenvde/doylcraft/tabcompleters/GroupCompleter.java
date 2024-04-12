package dev.siebrenvde.doylcraft.tabcompleters;

import dev.siebrenvde.doylcraft.handlers.LuckPermsHandler;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.luckperms.api.model.group.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GroupCompleter implements TabCompleter {

    private LuckPermsHandler handler;

    public GroupCompleter(LuckPermsHandler lp) {
        handler = lp;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        if(args.length == 2) {
            List<String> groups = new ArrayList<>();
            for(Group group : handler.getGroups()) {
                groups.add(group.getName());
            }
            return Utils.autoCompleter(args[1], groups);
        }

        return null;
    }

}
