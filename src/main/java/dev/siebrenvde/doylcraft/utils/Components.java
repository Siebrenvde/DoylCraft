package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

    /**
     * Builds an entity component onto an existing {@link Component}
     * @param component the component to convert
     * @param entity the entity
     * @return a new entity component
     */
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

    /**
     * Builds an entity component
     * @param entity the entity
     * @return a new entity component
     */
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

    /**
     * Builds an entity component from a {@link Player}
     * @param player the player
     * @return a new entity component
     */
    public static Component entity(Player player) {
        return entity((Entity) player);
    }

    /**
     * Builds an entity component from an {@link OfflinePlayer}
     * @param offlinePlayer the player
     * @return a new entity component
     */
    public static Component entity(OfflinePlayer offlinePlayer) {
        if(offlinePlayer == null || offlinePlayer.getName() == null) return Component.text("Unknown Player");
        if(offlinePlayer.isOnline()) return entity(offlinePlayer.getPlayer());
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

    /**
     * Builds a world name component
     * @param world the world
     * @return a new world name component
     */
    public static Component worldName(World world) {
        return Component.text(world.getKey().getKey()).hoverEvent(HoverEvent.showText(Component.text(world.getKey().toString())));
    }

    /**
     * Builds an elapsed time component
     * @param secondsL the elapsed time in seconds
     * @return a new elapsed time component
     */
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

    /**
     * Builds a timestamp component
     * @param timestamp the timestamp in milliseconds since the Unix epoch
     * @return a new timestamp component
     */
    public static Component timestamp(long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a").withZone(ZoneId.from(ZoneOffset.UTC));
        return Component.text(formatter.format(Instant.ofEpochMilli(timestamp)));
    }

    /**
     * Builds an exception component
     * @param message the message to show the command sender
     * @param exception the exception
     * @return a new exception component
     */
    public static Component exception(Component message, Exception exception) {
        TextComponent.Builder builder = Component.text();
        builder.content(exception.getClass().getSimpleName()).color(Colours.ERROR);
        if(exception.getMessage() != null) {
            builder.appendNewline();
            builder.append(Component.text(exception.getMessage(), Colours.DATA));
        }
        return message.hoverEvent(HoverEvent.showText(builder.build()));
    }

}
