package dev.siebrenvde.doylcraft.utils;

import org.bukkit.ChatColor;

public class Messages {

    public static String permissionMessage = ChatColor.RED + "You don't have permission to use this command.";

    public static String usageMessage(String usage) {
        return ChatColor.RED + "Usage: " + usage;
    }
    public static String usageMessage(CommandUsage commandUsage) { return usageMessage(commandUsage.usage); }

    public enum CommandUsage {
        // TEMP: Move to file later

        MAINTENANCE("/maintenance [<command>] [-k/<player>]"),
        PLAYTIME("/playtime [<player>]"),
        PVP("/pvp [on/off] [<world>]"),
        RANK("/rank <player> [<group>]");

        public final String usage;
        private CommandUsage(String usage) {
            this.usage = usage;
        }
    }

}
