package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerDeath(PlayerDeathEvent event) {
        Entity entity = event.getDamageSource().getCausingEntity();
        if (entity == null || entity instanceof Player) return;
        PlayerData.revengeList(event.getPlayer()).add(entity);
    }

}
