package dev.siebrenvde.doylcraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {

    public static void broadcastMessage(String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

}
