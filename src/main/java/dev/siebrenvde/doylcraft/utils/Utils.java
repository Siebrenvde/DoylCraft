package dev.siebrenvde.doylcraft.utils;

import dev.siebrenvde.doylcraft.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.*;

public class Utils {

    public static void broadcastMessage(Component message) {
        Main.getInstance().getServer().broadcast(message);
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

    public static Component worldNameComponent(World world) {
        return Component.text(world.getKey().getKey()).hoverEvent(HoverEvent.showText(Component.text(world.getKey().toString())));
    }

}
