package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.commands.GetOwnerCommand;
import dev.siebrenvde.doylcraft.commands.SilenceCommand;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Listener for {@link PlayerInteractEntityEvent}
 */
@NullMarked
public class PlayerInteractEntityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(MemoryHandler.GET_OWNER_PLAYERS.contains(player)) {
            event.setCancelled(true);
            MemoryHandler.GET_OWNER_PLAYERS.remove(player);
            GetOwnerCommand.handle(player, entity);
        }

        if(MemoryHandler.SILENCE_PLAYERS.containsKey(player)) {
            event.setCancelled(true);
            switch(MemoryHandler.SILENCE_PLAYERS.get(player)) {
                case QUERY -> SilenceCommand.query(player, entity);
                case SET_TRUE -> SilenceCommand.set(player, entity, true);
                case SET_FALSE -> SilenceCommand.set(player, entity, false);
            }
            MemoryHandler.SILENCE_PLAYERS.remove(player);
        }
    }

}
