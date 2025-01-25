package dev.siebrenvde.doylcraft.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Listener for {@link PlayerInteractEntityEvent}
 * <p>
 * Dismount an entity from a vehicle by shift-right-clicking the vehicle
 */
public class DismountEntityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerEntityInteract(PlayerInteractEntityEvent e) {

        if(!(e.getRightClicked() instanceof Vehicle vehicle)) { return; }

        if(!e.getPlayer().isSneaking()) { return; }

        for(Entity entity : vehicle.getPassengers()) {
            if(!(entity instanceof Player)) {
                vehicle.removePassenger(entity);
            }
        }

    }

}
