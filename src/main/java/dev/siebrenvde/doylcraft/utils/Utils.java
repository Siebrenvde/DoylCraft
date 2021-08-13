package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {

    public static void broadcastMessage(String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    public static void broadcastHover(String streamer, String title, String url) {
        for(Player p : Bukkit.getOnlinePlayers()) {

            TextComponent text = Component.text("[").color(TextColor.fromHexString("#6441a5"))
                    .append(Component.text("Twitch").color(TextColor.fromHexString("#6441a5")))
                    .append(Component.text("] ").color(TextColor.fromHexString("#6441a5")))
                    .append(Component.text(streamer).color(TextColor.fromHexString("#6441a5")))
                    .append(Component.text(" is now live!").color(TextColor.fromHexString("#6441a5")));

            TextComponent hoverText = Component.text(title);
            text = text.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

            text = text.clickEvent(ClickEvent.openUrl(url));

            p.sendMessage(text);

        }
    }

}
