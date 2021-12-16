package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Utils;
import dev.siebrenvde.tameableaxolotls.handlers.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class TameableInteractEvent implements Listener {

    private Main main;
    private DataHandler dataHandler;

    public TameableInteractEvent(Main main) {
        this.main = main;
        this.dataHandler = new DataHandler();
    }

    @EventHandler
    public void onTameableInteract(PlayerInteractEntityEvent event) {

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if(main.listContains(player)) {

            event.setCancelled(true);

            if(entity instanceof Axolotl) {

                if(dataHandler.hasOwner(entity)) {
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(dataHandler.getOwner(entity)));
                    player.sendMessage(ChatColor.GREEN + "Axolotl's owner is " + owner.getName() + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "Axolotl doesn't have an owner.");
                }

            }

            else if(entity instanceof Tameable) {

                Tameable e = (Tameable) entity;
                String type = (Utils.getTameableName(e) != null) ? Utils.getTameableName(e) : "Unknown Entity";

                if(e.isTamed()) {
                    OfflinePlayer owner = (OfflinePlayer) e.getOwner();
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
