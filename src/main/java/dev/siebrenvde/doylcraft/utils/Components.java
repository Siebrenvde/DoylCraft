package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.*;

public class Components {

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
