package dev.siebrenvde.doylcraft.handlers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldGuardHandler {

    public static StateFlag ALLOW_SETHOME_COMMAND = new StateFlag("allow-sethome-command", true);
    public static StateFlag ALLOW_TELEPORT = new StateFlag("allow-teleport", true);

    private final RegionContainer container;

    public WorldGuardHandler() {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public static void registerFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(ALLOW_SETHOME_COMMAND);
        registry.register(ALLOW_TELEPORT);
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

    public ApplicableRegionSet getApplicableRegions(Location location) {
        return getRegionManager(location.getWorld())
            .getApplicableRegions(
                BlockVector3.at(
                    location.getX(),
                    location.getY(),
                    location.getZ()
                )
            );
    }

}
