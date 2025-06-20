package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Listener for {@link TargetHitEvent}
 * <p>
 * Broadcasts a message when a player hits a bullseye on a target block from at least 30 blocks away
 */
@NullMarked
public class BullseyeListener implements Listener {

    private static final int BULLSEYE_SIGNAL_STRENGTH = 15;
    private static final int MIN_BROADCAST_DISTANCE = 30;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTargetHit(TargetHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (event.getSignalStrength() != BULLSEYE_SIGNAL_STRENGTH) return;

        double distance = player.getLocation().distance(Objects.requireNonNull(event.getHitBlock()).getLocation());
        if (distance < MIN_BROADCAST_DISTANCE) return;

        Component message = Component.text()
            .append(Components.entity(player))
            .append(Component.text(" hit a bullseye from " + ((int) distance) + " blocks away!"))
            .color(NamedTextColor.LIGHT_PURPLE)
            .build();

        Bukkit.broadcast(message);
        DiscordSRVAddon.get().sendEmbed(
            DiscordSRVAddon.GLOBAL_CHANNEL,
            DiscordSRVAddon.playerEmbed(player, message)
        );
    }

}
