package dev.siebrenvde.doylcraft.utils;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.NullMarked;

import java.util.function.Predicate;

@NullMarked
public class Permissions {

    public static final CommandPermission COMMAND_CONFIG = command("config");
    public static final CommandPermission COMMAND_DEBUG = command("debug");
    public static final CommandPermission COMMAND_UTILS_HIGHLIGHT_ENTITIES = command("utils.highlight-entities");
    public static final CommandPermission COMMAND_GETOWNER_SELECTOR = command("getowner.selector");
    public static final CommandPermission COMMAND_GROUP = command("group");
    public static final CommandPermission COMMAND_PVP_QUERY = command("pvp.query");
    public static final CommandPermission COMMAND_PVP_UPDATE = command("pvp.update");
    public static final CommandPermission COMMAND_SILENCE_QUERY_SELECTOR = command("silence.query.selector");
    public static final CommandPermission COMMAND_SILENCE_SET_SELECTOR = command("silence.set.selector");
    public static final CommandPermission COMMAND_SETWARP = command("setwarp");
    public static final CommandPermission COMMAND_DELWARP = command("delwarp");
    public static final CommandPermission COMMAND_WARPS_RELOAD = command("warps.reload");
    public static final CommandPermission COMMAND_WARPS_EDIT = command("warps.edit");

    private static CommandPermission command(String command) {
        CommandPermission perm = new CommandPermission("doylcraft.command." + command);
        Bukkit.getPluginManager().addPermission(perm);
        return perm;
    }

    public static class CommandPermission extends Permission implements Predicate<CommandSourceStack> {

        private CommandPermission(String name) {
            super(name);
        }

        @Override
        public boolean test(CommandSourceStack source) {
            return source.getSender().hasPermission(this);
        }

    }

}
