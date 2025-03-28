package dev.siebrenvde.doylcraft.addons;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import dev.siebrenvde.doylcraft.warp.Warp;
import dev.siebrenvde.doylcraft.warp.Warps;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

/**
 * BlueMap addon for adding warp markers
 */
@NullMarked
public class BlueMapAddon {

    @Nullable private static BlueMapAddon instance;

    @Nullable private BlueMapAPI bluemap;
    private final Map<BlueMapWorld, MarkerSet> markerSets = new HashMap<>();

    public BlueMapAddon() {
        instance = this;
        BlueMapAPI.onEnable(api -> {
            bluemap = api;
            populateWarps();
        });
    }

    /**
     * Creates a new MarkerSet for a given world
     *
     * @param world the world the MarkerSet should be for
     */
    private void createMarkerSet(BlueMapWorld world) {
        MarkerSet markerSet = new MarkerSet("Warps");
        markerSets.put(world, markerSet);
        world.getMaps().forEach(map -> map.getMarkerSets().put("doylcraft-warps", markerSet));
    }

    /**
     * Adds a new warp marker at the given location
     * @param warp the warp
     */
    public void addMarker(Warp warp) {
        Location loc = warp.location();
        bluemap().getWorld(loc.getWorld()).ifPresent(world -> {
            if (!markerSets.containsKey(world)) createMarkerSet(world);
            POIMarker marker = new POIMarker(
                warp.key(),
                new Vector3d(loc.x(), loc.y(), loc.z())
            );
            marker.setDetail(plainText().serialize(warp.displayName()));
            markerSets.get(world).put(warp.key(), marker);
        });
    }

    /**
     * Removes an existing warp marker
     * @param warp the warp
     */
    public void removeMarker(Warp warp) {
        bluemap().getWorld(warp.location().getWorld()).ifPresent(world -> {
            if (!markerSets.containsKey(world)) return;
            markerSets.get(world).remove(warp.key());
        });
    }

    /**
     * Updates an existing warp marker
     * @param oldWarp the old warp
     * @param newWarp the new warp
     */
    public void updateMarker(Warp oldWarp, Warp newWarp) {
        removeMarker(oldWarp);
        addMarker(newWarp);
    }

    /**
     * Adds all existing warps as markers
     */
    public void populateWarps() {
        markerSets.clear();
        Warps.WARPS.values().forEach(this::addMarker);
    }

    public static BlueMapAddon get() { return Objects.requireNonNull(instance); }
    private BlueMapAPI bluemap() { return Objects.requireNonNull(bluemap); }

}
