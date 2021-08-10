package dev.siebrenvde.doylcraft.utils;

import org.bukkit.ChatColor;

public class Messages {

    public static String permissionMessage = ChatColor.RED + "You don't have permission to use this command.";

    public static String usageMessage(String usage) {
        return ChatColor.RED + "Usage: " + usage;
    }

}
