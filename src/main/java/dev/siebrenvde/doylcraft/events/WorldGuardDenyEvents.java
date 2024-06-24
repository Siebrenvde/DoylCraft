package dev.siebrenvde.doylcraft.events;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.siebrenvde.doylcraft.handlers.WorldGuardHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Arrays;
import java.util.List;

public class WorldGuardDenyEvents implements Listener {

    private final WorldGuardHandler worldGuardHandler;

    public WorldGuardDenyEvents(WorldGuardHandler worldGuardHandler) {
        this.worldGuardHandler = worldGuardHandler;
    }

    @EventHandler
    private void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if(!event.getMessage().startsWith("/sethome")) return; // TODO: Add aliases
        Player player = event.getPlayer();
        if(isDenied(player, WorldGuardHandler.ALLOW_SETHOME_COMMAND)) {
            player.sendMessage("no"); // TODO: Proper message
            event.setCancelled(true);
        }
    }

    private static final List<TeleportCause> DISALLOWED_TELEPORT_CAUSES = Arrays.stream(new TeleportCause[]{
        TeleportCause.CHORUS_FRUIT,
        TeleportCause.COMMAND,
        TeleportCause.ENDER_PEARL,
        TeleportCause.PLUGIN
    }).toList();

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if(!DISALLOWED_TELEPORT_CAUSES.contains(event.getCause())) return;
        Player player = event.getPlayer();
        if(isDenied(player, WorldGuardHandler.ALLOW_TELEPORT)) {
            player.sendMessage("no"); // TODO: Proper message
            event.setCancelled(true);
        }
    }

    private boolean isDenied(Player player, StateFlag flag) {
        for(ProtectedRegion region : worldGuardHandler.getApplicableRegions(player.getLocation())) {
            if(region.getFlag(flag) == StateFlag.State.DENY) return true;
        }
        return false;
    }

}
