package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Listener for {@link PlayerInteractEntityEvent}
 * <p>
 * If a player executed {@link dev.siebrenvde.doylcraft.commands.GetOwnerCommand},
 * sends them a message with the name of the entity's owner
 */
public class TameableInteractListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTameableInteract(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();

        if(!MemoryHandler.GET_OWNER_PLAYERS.contains(player)) return;

        event.setCancelled(true);
        MemoryHandler.GET_OWNER_PLAYERS.remove(player);

        Entity entity = event.getRightClicked();

        if(entity instanceof Tameable tameable) {
            if(tameable.isTamed() && tameable.getOwner() != null) {
                OfflinePlayer owner = (OfflinePlayer) tameable.getOwner();
                player.sendMessage(
                    Component.empty()
                    .append(Components.entity(entity).color(Colours.DATA))
                    .append(Component.text("'s owner is ", Colours.GENERIC))
                    .append(Components.entity(owner).color(Colours.DATA))
                );
                return;
            }
        }

        player.sendMessage(
            Component.empty()
            .append(Components.entity(entity).color(Colours.DATA))
            .append(Component.text(" doesn't have an owner", Colours.GENERIC))
        );

    }

}
