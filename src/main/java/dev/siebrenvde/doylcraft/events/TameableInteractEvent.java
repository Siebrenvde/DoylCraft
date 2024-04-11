package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TameableInteractEvent implements Listener {

    private Main main;

    public TameableInteractEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onTameableInteract(PlayerInteractEntityEvent event) {

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if(main.listContains(player)) {

            event.setCancelled(true);

            if(entity instanceof Tameable tameable) {

                String type = (Utils.getTameableName(tameable) != null) ? Utils.getTameableName(tameable) : "Unknown Entity";

                if(tameable.isTamed()) {
                    OfflinePlayer owner = (OfflinePlayer) tameable.getOwner();
                    player.sendMessage(ChatColor.GREEN + type + "'s owner is " + owner.getName() + ".");
                } else {
                    player.sendMessage(ChatColor.RED + type + " doesn't have an owner.");
                }

            }

            else {
                player.sendMessage(ChatColor.RED + "This entity doesn't have an owner.");
            }

            main.removeListPlayer(player);

        }

    }

}
