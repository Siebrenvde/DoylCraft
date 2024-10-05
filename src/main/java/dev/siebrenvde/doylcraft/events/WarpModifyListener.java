package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.BlueMapHandler;
import net.essentialsx.api.v2.events.WarpModifyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener for Essentials' WarpModifyEvent
 */
public class WarpModifyListener implements Listener {

    private final BlueMapHandler blueMapHandler;

    public WarpModifyListener(BlueMapHandler blueMapHandler) {
        this.blueMapHandler = blueMapHandler;
    }

    @EventHandler
    private void onWarpModify(WarpModifyEvent event) {
        switch (event.getCause()) {
            case CREATE -> blueMapHandler.addMarker(event.getWarpName(), event.getNewLocation());
            case DELETE -> blueMapHandler.removeMarker(event.getWarpName(), event.getOldLocation());
            case UPDATE -> blueMapHandler.updateMarker(event.getWarpName(), event.getOldLocation(), event.getNewLocation());
        }
    }

}
