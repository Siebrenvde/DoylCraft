package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.*;

public class Components {

    public static Component entityComponent(Component component, Entity entity) {
        return Component.text()
            .append(component)
            .hoverEvent(entity.asHoverEvent())
            .clickEvent(
                entity instanceof Player player
                    ? ClickEvent.suggestCommand("/msg " + player.getName() + " ")
                    : ClickEvent.copyToClipboard(entity.getUniqueId().toString())
            )
            .build();
    }

    public static Component entityComponent(Entity entity) {
        return entityComponent(
            entity instanceof Player player
                ? Component.text(player.getName())
                : entity.customName() != null
                    ? entity.customName()
                    : Component.translatable(entity.getType()),
            entity
        );
    }

    public static Component entityComponent(Player player) {
        return entityComponent((Entity) player);
    }

    public static Component entityComponent(OfflinePlayer offlinePlayer) {
        if(offlinePlayer == null || offlinePlayer.getName() == null) return Component.text("Unknown Player");
        return Component.text()
            .content(offlinePlayer.getName())
            .hoverEvent(HoverEvent.showEntity(
                EntityType.PLAYER,
                offlinePlayer.getUniqueId(),
                Component.text(offlinePlayer.getName())
            ))
            .clickEvent(ClickEvent.suggestCommand("/mail send " + offlinePlayer.getName() + " "))
            .build();
    }

    public static Component worldNameComponent(World world) {
        return Component.text(world.getKey().getKey()).hoverEvent(HoverEvent.showText(Component.text(world.getKey().toString())));
    }

}
