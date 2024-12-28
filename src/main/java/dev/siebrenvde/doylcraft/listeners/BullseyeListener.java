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

/**
 * Listener for {@link TargetHitEvent}
 * <p>
 * Broadcasts a message when a player hits a bullseye on a target block from at least 30 blocks away
 */
public class BullseyeListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTargetHit(TargetHitEvent event) {

        ProjectileSource shooter = event.getEntity().getShooter();

        if(shooter instanceof Player player) {

            if(event.getSignalStrength() == 15) {
                double distance = player.getLocation().distance(event.getHitBlock().getLocation());
                if(distance >= 30) {
                    Bukkit.broadcast(
                        Component.empty()
                        .append(Components.entity(player).color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" hit a bullseye from " + ((int) distance) + " blocks away!", NamedTextColor.LIGHT_PURPLE))
                    );
                }
            }

        }

    }

}
