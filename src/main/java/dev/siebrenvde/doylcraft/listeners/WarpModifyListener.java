package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.BlueMapAddon;
import net.essentialsx.api.v2.events.WarpModifyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listener for Essentials' {@link WarpModifyEvent}
 * <p>
 * Updates the BlueMap marker set when a warp is added, removed or updated
 */
public class WarpModifyListener implements Listener {

    private final BlueMapAddon blueMapAddon;

    public WarpModifyListener(BlueMapAddon blueMapAddon) {
        this.blueMapAddon = blueMapAddon;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onWarpModify(WarpModifyEvent event) {
        switch (event.getCause()) {
            case CREATE -> blueMapAddon.addMarker(event.getWarpName(), event.getNewLocation());
            case DELETE -> blueMapAddon.removeMarker(event.getWarpName(), event.getOldLocation());
            case UPDATE -> blueMapAddon.updateMarker(event.getWarpName(), event.getOldLocation(), event.getNewLocation());
        }
    }

}
