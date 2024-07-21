package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.utils.Utils;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
                double distance = player.getLocation().distance(event.getHitBlock().getLocation());
                if(distance >= 30) {
                    Bukkit.broadcast(
                        Component.empty()
                        .append(Utils.entityComponent(Component.text(player.getName(), NamedTextColor.LIGHT_PURPLE), player))
                        .append(Component.text(" hit a bullseye from " + ((int) distance) + " blocks away!", NamedTextColor.LIGHT_PURPLE))
                    );
                }
            }

        }

    }

}
