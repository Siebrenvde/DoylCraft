package dev.siebrenvde.doylcraft.utils;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static void broadcastMessage(String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    public static void broadcastHover(String streamer, String title, String url) {
        for(Player p : Bukkit.getOnlinePlayers()) {

            TextComponent text = Component.text("[Twitch]").color(Colours.twitch)
                    .append(Component.text(streamer).color(Colours.twitch))
                    .append(Component.text(" is now live!").color(Colours.twitch));

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

    public static String getStreamDuration(String timeString) {

        // Set time format to Twitch API format (ISO 8601)
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        // Set input timezone to UTC to convert to local time correctly
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startTime;
        try {
            startTime = df.parse(timeString);
        } catch(ParseException e) {
            e.printStackTrace();
            return "Unknown";
        }

        // Get current time in local time
        Date now = new Date();

        long difference = now.getTime() - startTime.getTime();

        // Divide by 1000 because formatTime uses seconds
        return TimeHandler.formatTime(difference / 1000);

    }

}
