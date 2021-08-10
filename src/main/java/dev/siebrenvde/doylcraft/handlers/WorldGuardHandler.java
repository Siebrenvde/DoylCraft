package dev.siebrenvde.doylcraft.handlers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public class WorldGuardHandler {

    private RegionContainer container;

    public WorldGuardHandler() {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        return container.get(BukkitAdapter.adapt(world));
    }

    public ProtectedRegion getRegion(String world, String region) {
        World bukkitWorld = Bukkit.getWorld(world);
        return getRegionManager(bukkitWorld).getRegion(region);
    }

    private BlockVector3 getBlockVector3(Location l) {
        return BlockVector3.at(l.getX(), l.getY(), l.getZ());
    }

    public boolean locationInRegion(Location location) {
        Set<ProtectedRegion> regions = getRegionManager(location.getWorld()).getApplicableRegions(getBlockVector3(location)).getRegions();
        if(regions.contains(getRegion("world", "kamile-axolotls"))) {
            return true;
        } else {
            return false;
        }

    }

}
