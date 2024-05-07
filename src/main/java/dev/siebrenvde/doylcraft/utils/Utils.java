package dev.siebrenvde.doylcraft.utils;

import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    public static void broadcastMessage(String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    public static void broadcastMessage(Component message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    public static void broadcastHover(String streamer, String title, String url) {
        for(Player p : Bukkit.getOnlinePlayers()) {

            TextComponent text = Component.text("[Twitch]").color(Colours.TWITCH)
                    .append(Component.text(streamer).color(Colours.TWITCH))
                    .append(Component.text(" is now live!").color(Colours.TWITCH));

            TextComponent hoverText = Component.text(title);
            text = text.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

            text = text.clickEvent(ClickEvent.openUrl(url));

            p.sendMessage(text);

        }
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

    public static List<String> autoCompleter(String arg, List<String> list) {
        if(arg.length() > 0) {
            List<String> nList = new ArrayList<>();
            for(String s : list) {
                if(s.toLowerCase().startsWith(arg.toLowerCase())) {
                    nList.add(s);
                }
            }
            return nList;
        }

        return list;
    }

    public static Component entityComponent(Component component, EntityType type, UUID uuid, Component name) {
        component = component.hoverEvent(HoverEvent.showEntity(
                type,
                uuid,
                name != null ? name : Component.translatable(type)
        ));
        if(type == EntityType.PLAYER && name != null) {
            component = component.clickEvent(ClickEvent.suggestCommand("/msg " + ((TextComponent)name).content() + " "));
        } else {
            component = component.clickEvent(ClickEvent.copyToClipboard(uuid.toString()));
        }
        return component;
    }

    public static Component entityComponent(Component component, Entity entity) {
        return entityComponent(component, entity.getType(), entity.getUniqueId(), entity.customName());
    }

    public static Component entityComponent(Component component, OfflinePlayer offlinePlayer) {
        return entityComponent(component, EntityType.PLAYER, offlinePlayer.getUniqueId(), Component.text(offlinePlayer.getName()));
    }

    public static Component entityComponent(Component component, Player player) {
        return entityComponent(component, EntityType.PLAYER, player.getUniqueId(), Component.text(player.getName()));
    }

}
