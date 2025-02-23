package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

/**
 * Listener for {@link PlayerDeepSleepEvent}
 * <p>
 * Broadcasts a message to the server and Discord when a player sleeps and skips the night
 */
@NullMarked
public class PlayerSleepListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onDeepSleep(PlayerDeepSleepEvent event) {
        Component message = text()
            .append(Components.entity(event.getPlayer()))
            .append(text(" went to sleep. Good morning!"))
            .colorIfAbsent(NamedTextColor.AQUA)
            .build();
        Bukkit.broadcast(message);
        DiscordSRVAddon.get().sendDiscordMessage(
            "global",
            text(":sunrise: **").append(message).append(text("**"))
        );
    }

}
