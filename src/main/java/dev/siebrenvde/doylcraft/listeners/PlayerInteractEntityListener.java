package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.commands.GetOwnerCommand;
import dev.siebrenvde.doylcraft.commands.SilenceCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jspecify.annotations.NullMarked;

import static dev.siebrenvde.doylcraft.commands.GetOwnerCommand.GET_OWNER_PLAYERS;
import static dev.siebrenvde.doylcraft.commands.SilenceCommand.SILENCE_PLAYERS;

/**
 * Listener for {@link PlayerInteractEntityEvent}
 */
@NullMarked
public class PlayerInteractEntityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(GET_OWNER_PLAYERS.contains(player)) {
            event.setCancelled(true);
            GET_OWNER_PLAYERS.remove(player);
            GetOwnerCommand.handle(player, entity);
        }

        if(SILENCE_PLAYERS.containsKey(player)) {
            event.setCancelled(true);
            switch(SILENCE_PLAYERS.get(player)) {
                case QUERY -> SilenceCommand.query(player, entity);
                case SET_TRUE -> SilenceCommand.set(player, entity, true);
                case SET_FALSE -> SilenceCommand.set(player, entity, false);
            }
            SILENCE_PLAYERS.remove(player);
        }
    }

}
