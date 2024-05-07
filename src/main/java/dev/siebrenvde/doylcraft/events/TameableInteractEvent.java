package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
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
        TranslatableComponent type = Component.translatable(entity.getType());

        if(entity instanceof Tameable tameable) {

            if(tameable.isTamed()) {
                OfflinePlayer owner = (OfflinePlayer) tameable.getOwner();
                player.sendMessage(
                    Component.empty()
                    .append(Utils.entityComponent(type.color(Colours.DATA), entity))
                    .append(Component.text("'s owner is ", Colours.GENERIC))
                    .append(Utils.entityComponent(Component.text(owner.getName() != null ? owner.getName() : "Unknown Player", Colours.DATA), player))
                    .append(Component.text(".", Colours.GENERIC))
                );
                return;
            }
        }

        player.sendMessage(
            Component.empty()
            .append(Utils.entityComponent(type.color(Colours.DATA), entity))
            .append(Component.text(" doesn't have an owner.", Colours.GENERIC))
        );

    }

}
