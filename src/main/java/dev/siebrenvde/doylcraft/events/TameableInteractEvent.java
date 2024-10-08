package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TameableInteractEvent implements Listener {

    private final MemoryHandler memoryHandler;

    public TameableInteractEvent(MemoryHandler memoryHandler) {
        this.memoryHandler = memoryHandler;
    }

    @EventHandler
    public void onTameableInteract(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();

        if(!memoryHandler.getOwnerListContains(player)) { return; }

        event.setCancelled(true);
        memoryHandler.removeGetOwnerPlayer(player);

        Entity entity = event.getRightClicked();

        if(entity instanceof Tameable tameable) {
            if(tameable.isTamed() && tameable.getOwner() != null) {
                OfflinePlayer owner = (OfflinePlayer) tameable.getOwner();
                player.sendMessage(
                    Component.empty()
                    .append(Components.entity(entity).color(Colours.DATA))
                    .append(Component.text("'s owner is ", Colours.GENERIC))
                    .append(Components.entity(owner).color(Colours.DATA))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return;
            }
        }

        player.sendMessage(
            Component.empty()
            .append(Components.entity(entity).color(Colours.DATA))
            .append(Component.text(" doesn't have an owner.", Colours.GENERIC))
        );

    }

}
