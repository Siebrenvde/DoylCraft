package dev.siebrenvde.doylcraft.utils;

import dev.siebrenvde.doylcraft.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {

    public static void broadcastMessage(String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    public static void broadcastHover(String streamer, String title, String url) {
        for(Player p : Bukkit.getOnlinePlayers()) {

            TextComponent text = Component.text("[Twitch]").color(TextColor.fromHexString("#6441a5"))
                    .append(Component.text(streamer).color(TextColor.fromHexString("#6441a5")))
                    .append(Component.text(" is now live!").color(TextColor.fromHexString("#6441a5")));

            TextComponent hoverText = Component.text(title);
            text = text.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

            text = text.clickEvent(ClickEvent.openUrl(url));

            p.sendMessage(text);

        }
    }

    public static void removeListCountdown(Main main, Player player) {

        new BukkitRunnable() {
            @Override
            public void run() {

                if(main.listContains(player)) {
                    main.removeListPlayer(player);
                }

            }
        }.runTaskLaterAsynchronously(main, 200L);

    }

    public static String getTameableName(Tameable e) {
        String type = null;
        if(e instanceof Cat) { type = "Cat"; }
        if(e instanceof Wolf) { type = "Wolf"; }
        if(e instanceof Parrot) { type = "Parrot"; }
        if(e instanceof Horse) { type = "Horse"; }
        if(e instanceof Donkey) { type = "Donkey"; }
        if(e instanceof Mule) { type = "Mule"; }
        if(e instanceof Llama) { type = "Llama"; }
        if(e instanceof SkeletonHorse) { type = "Skeleton Horse"; }
        if(e instanceof ZombieHorse) { type = "Zombie Horse"; }
        if(e instanceof TraderLlama) { type = "Trader Llama"; }
        return type;
    }

}
