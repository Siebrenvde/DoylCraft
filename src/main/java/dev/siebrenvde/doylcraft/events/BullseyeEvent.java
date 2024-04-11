package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.utils.Utils;
import io.papermc.paper.event.block.TargetHitEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;

public class BullseyeEvent implements Listener {

    @EventHandler
    public void onTargetHit(TargetHitEvent event) {

        ProjectileSource shooter = event.getEntity().getShooter();

        if(shooter instanceof Player player) {

            if(event.getSignalStrength() == 15) {
                if(player.getLocation().distance(event.getHitBlock().getLocation()) >= 30) {
                    Utils.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " hit a bullseye!");
                }
            }

        }

    }

}
