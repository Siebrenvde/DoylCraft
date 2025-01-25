package dev.siebrenvde.doylcraft.listeners;

import io.papermc.paper.event.player.PlayerNameEntityEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listener for {@link PlayerNameEntityEvent}
 * <p>
 * Silences an entity if it is named <i>shut up</i> or <i>silenced</i>
 */
public class SilenceEntityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityNameTag(PlayerNameEntityEvent event) {
        if(event.getName() == null) return;
        String name = PlainTextComponentSerializer.plainText().serialize(event.getName());
        if(name.equalsIgnoreCase("shut up") || name.equalsIgnoreCase("silenced")) {
            event.getEntity().setSilent(true);
        }
    }

}
