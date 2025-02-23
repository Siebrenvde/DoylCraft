package dev.siebrenvde.doylcraft.addons;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public class WorldGuardAddon {

    @Nullable private static WorldGuardAddon instance;
    private final RegionContainer container;

    public WorldGuardAddon() {
        instance = this;
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        return Objects.requireNonNull(container.get(BukkitAdapter.adapt(world)));
    }

    public static WorldGuardAddon get() {
        return Objects.requireNonNull(instance);
    }

    /**
     * {@return the region for the given name and world}
     * @param world the world the region is in
     * @param region the name of the region
     */
    public Optional<ProtectedRegion> getRegion(World world, String region) {
        return Optional.ofNullable(getRegionManager(world).getRegion(region));
    }

    /**
     * {@return the global region for the specified world}
     * <p>
     * Creates the region if it does not yet exist
     * @param world the world
     */
    public ProtectedRegion getOrCreateGlobalRegion(World world) {
        ProtectedRegion region = getRegionManager(world).getRegion("__global__");
        if (region == null) {
            region = new GlobalProtectedRegion("__global__");
            getRegionManager(world).addRegion(region);
        }
        return region;
    }

}
