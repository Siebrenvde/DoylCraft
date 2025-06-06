package dev.siebrenvde.doylcraft.addons;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public class WorldGuardAddon {

    private static final String GLOBAL_REGION_ID = "__global__";

    private static final RegionContainer container;

    private static RegionManager getRegionManager(World world) {
        return Objects.requireNonNull(container.get(BukkitAdapter.adapt(world)));
    }

    /**
     * {@return the region for the given name and world}
     * @param world the world the region is in
     * @param region the name of the region
     */
    public static Optional<ProtectedRegion> getRegion(World world, String region) {
        return Optional.ofNullable(getRegionManager(world).getRegion(region));
    }

    /**
     * {@return the global region for the specified world}
     * <p>
     * Creates the region if it does not yet exist
     * @param world the world
     */
    public static ProtectedRegion getOrCreateGlobalRegion(World world) {
        return Optional.ofNullable(getRegionManager(world).getRegion(GLOBAL_REGION_ID))
            .orElseGet(() -> {
                ProtectedRegion region = new GlobalProtectedRegion(GLOBAL_REGION_ID);
                getRegionManager(world).addRegion(region);
                return region;
            });
    }

    static {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

}
