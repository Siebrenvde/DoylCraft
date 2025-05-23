package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Listener for {@link TargetHitEvent}
 * <p>
 * Broadcasts a message when a player hits a bullseye on a target block from at least 30 blocks away
 */
@NullMarked
public class BullseyeListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTargetHit(TargetHitEvent event) {

        ProjectileSource shooter = event.getEntity().getShooter();

        if(shooter instanceof Player player) {

            if(event.getSignalStrength() == 15) {
                double distance = player.getLocation().distance(Objects.requireNonNull(event.getHitBlock()).getLocation());
                if(distance >= 30) {
                    Bukkit.broadcast(
                        Component.text()
                            .append(Components.entity(player))
                            .append(Component.text(" hit a bullseye from " + ((int) distance) + " blocks away!"))
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .build()
                    );
                }
            }

        }

    }

}
