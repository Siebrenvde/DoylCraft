package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.time.Duration;
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
     * Builds a duration component
     * @param duration the duration
     * @return a new duration component
     */
    public static Component duration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        boolean hasDays = days > 0;
        boolean hasHours = hours > 0 || hasDays && (minutes > 0 || seconds > 0);
        boolean hasMinutes = minutes > 0 || hasHours && seconds > 0;
        boolean hasSeconds = seconds > 0 || duration.getSeconds() == 0;

        StringBuilder builder = new StringBuilder();

        if(hasDays) appendTimeUnit(builder, days, "day");
        if(hasHours) appendTimeUnit(builder, hours, "hour");
        if(hasMinutes) appendTimeUnit(builder, minutes, "minute");
        if(hasSeconds) appendTimeUnit(builder, seconds, "second");

        return Component.text(builder.toString());
    }

    private static void appendTimeUnit(StringBuilder builder, long time, String unit) {
        if(!builder.isEmpty()) builder.append(", ");
        builder.append(String.format("%s %s%s", time, unit, time != 1 ? "s" : ""));
    }

    /**
     * Builds a timestamp component
     * @param instant the instant
     * @return a new timestamp component
     */
    public static Component timestamp(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a").withZone(ZoneId.from(ZoneOffset.UTC));
        return Component.text(formatter.format(instant));
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
