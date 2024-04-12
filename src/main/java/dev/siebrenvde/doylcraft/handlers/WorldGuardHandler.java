package dev.siebrenvde.doylcraft.handlers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

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

    public void createGlobalRegions() {
        for(World world : Bukkit.getWorlds()) {
            GlobalProtectedRegion region = new GlobalProtectedRegion("__global__");
            getRegionManager(world).addRegion(region);
        }
    }

}
