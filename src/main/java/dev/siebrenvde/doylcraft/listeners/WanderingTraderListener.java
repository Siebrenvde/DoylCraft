package dev.siebrenvde.doylcraft.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

/**
 * Listener for {@link CreatureSpawnEvent}
 * <p>
 * Sends a message to nearby players when a Wandering Trader spawns
 */
@NullMarked
public class WanderingTraderListener implements Listener {

    private static final int SPAWN_RADIUS = 48;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if(!(event.getEntity() instanceof WanderingTrader trader)) return;
        if(!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) return;
        Component message = text("A Wandering Trader has arrived nearby...", NamedTextColor.BLUE);
        trader.getNearbyEntities(SPAWN_RADIUS, SPAWN_RADIUS, SPAWN_RADIUS).forEach(entity -> {
            if(entity instanceof Player player) {
                player.sendMessage(message);
                player.playSound(
                    player,
                    Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    1,
                    1
                );
            }
        });
    }

}
