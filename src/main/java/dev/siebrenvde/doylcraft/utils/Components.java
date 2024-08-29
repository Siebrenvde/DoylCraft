package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Components {

    public static Component entity(Component component, Entity entity) {
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

    public static Component entity(Entity entity) {
        return entity(
            entity instanceof Player player
                ? Component.text(player.getName())
                : entity.customName() != null
                    ? entity.customName()
                    : Component.translatable(entity.getType()),
            entity
        );
    }

    public static Component entity(Player player) {
        return entity((Entity) player);
    }

    public static Component entity(OfflinePlayer offlinePlayer) {
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

    public static Component worldName(World world) {
        return Component.text(world.getKey().getKey()).hoverEvent(HoverEvent.showText(Component.text(world.getKey().toString())));
    }

    public static Component elapsedTime(long secondsL) {

        // https://stackoverflow.com/questions/19667473/

        long minutesL = secondsL / 60;
        long hoursL = minutesL / 60;
        long days = hoursL / 24;

        long seconds = secondsL % 60;
        long minutes = minutesL % 60;
        long hours = hoursL % 24;

        boolean hasDays = days > 0;
        boolean hasHours = hours > 0 || hasDays && (minutes > 0 || seconds > 0);
        boolean hasMinutes = minutes > 0 || hasHours && seconds > 0;
        boolean hasSeconds = seconds > 0 || secondsL == 0;

        String s = "";

        if(hasDays) s += String.format("%s day%s", days, days != 1 ? "s" : "");

        if(hasHours) {
            if(hasDays) s += ", ";
            s += String.format("%s hour%s", hours, hours != 1 ? "s" : "");
        }

        if(hasMinutes) {
            if(hasHours) s += ", ";
            s += String.format("%s minute%s", minutes, minutes != 1 ? "s" : "");
        }

        if(hasSeconds) {
            if(hasMinutes) s += ", ";
            s += String.format("%s second%s", seconds, seconds != 1 ? "s" : "");
        }

        return Component.text(s);
    }

    public static Component timestamp(long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a").withZone(ZoneId.from(ZoneOffset.UTC));
        return Component.text(formatter.format(Instant.ofEpochMilli(timestamp)));
    }

}
