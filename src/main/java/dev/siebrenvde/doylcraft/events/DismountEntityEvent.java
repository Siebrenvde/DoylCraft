package dev.siebrenvde.doylcraft.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Dismount an entity from a vehicle by shift-right-clicking the vehicle
 */
public class DismountEntityEvent implements Listener {

    @EventHandler
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
