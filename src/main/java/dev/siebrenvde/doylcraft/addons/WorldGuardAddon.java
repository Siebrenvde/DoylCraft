package dev.siebrenvde.doylcraft.addons;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;

public class WorldGuardAddon {

    private RegionContainer container;

    public WorldGuardAddon() {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        return container.get(BukkitAdapter.adapt(world));
    }

    public ProtectedRegion getRegion(World world, String region) {
        return getRegionManager(world).getRegion(region);
    }

    public ProtectedRegion getOrCreateGlobalRegion(World world) {
        ProtectedRegion region = getRegionManager(world).getRegion("__global__");
        if (region == null) {
            region = new GlobalProtectedRegion("__global__");
            getRegionManager(world).addRegion(region);
        }
        return region;
    }

}
