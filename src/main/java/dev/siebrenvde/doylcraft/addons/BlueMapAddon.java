package dev.siebrenvde.doylcraft.addons;

import com.earth2me.essentials.api.IWarps;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import dev.siebrenvde.doylcraft.DoylCraft;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * BlueMap addon for adding Essentials warp markers
 */
public class BlueMapAddon {

    private BlueMapAPI bluemap;
    private Map<BlueMapWorld, MarkerSet> markerSets;

    public BlueMapAddon() {
        BlueMapAPI.onEnable(api -> {
            bluemap = api;
            markerSets = new HashMap<>();
            api.getWorlds().forEach(this::createMarkerSet);
            populateWarps();
        });
    }

    /**
     * Creates a new MarkerSet for a given world
     * @param world The world the MarkerSet should be for
     */
    private void createMarkerSet(BlueMapWorld world) {
        MarkerSet markerSet = new MarkerSet("Warps");
        markerSets.put(world, markerSet);
        world.getMaps().forEach(map -> map.getMarkerSets().put("doylcraft-warps", markerSet));
    }

    /**
     * Adds a new warp marker at the given location
     * @param label The name of the warp
     * @param location The location of the warp
     */
    public void addMarker(String label, Location location) {
        bluemap.getWorld(location.getWorld()).ifPresent(world -> {
            if(!markerSets.containsKey(world)) createMarkerSet(world);
            markerSets.get(world).put(
                label,
                new POIMarker("Warp: " + label, new Vector3d(location.getX(), location.getY(), location.getZ()))
            );
        });
    }

    /**
     * Removes an existing warp marker
     * @param label The name of the warp
     * @param location The location of the warp
     */
    public void removeMarker(String label, Location location) {
        bluemap.getWorld(location.getWorld()).ifPresent(world -> {
            if(!markerSets.containsKey(world)) return;
            markerSets.get(world).remove(label);
        });
    }

    /**
     * Updates an existing warp marker
     * @param label The name of the warp
     * @param oldLocation The old location of the warp
     * @param newLocation The new location of the warp
     */
    public void updateMarker(String label, Location oldLocation, Location newLocation) {
        removeMarker(label, oldLocation);
        addMarker(label, newLocation);
    }

    /**
     * Adds all existing Essentials warps as markers
     */
    @SuppressWarnings("deprecation")
    private void populateWarps() {
        IEssentials essentials = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if(essentials == null) {
            DoylCraft.LOGGER.error("Failed to populate warps: Essentials not found");
            return;
        }
        IWarps warps = essentials.getWarps(); // Why would you deprecate a class and then not provide a method to get the non-deprecated class
        warps.getList().forEach(warp -> {
            try {
                addMarker(warp, warps.getWarp(warp));
            } catch (Exception e) {
                DoylCraft.LOGGER.error("Failed to add warp '{}': {}: {}", warp, e.getClass().getSimpleName(), e.getMessage());
            }
        });
    }

}
