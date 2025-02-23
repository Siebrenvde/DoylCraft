package dev.siebrenvde.doylcraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;

/**
 * Listener for {@link EntityExplodeEvent}, {@link EntityChangeBlockEvent} and {@link EntityInteractEvent}
 * <p>
 * Prevents Creeper and Ghast explosions from destroying blocks
 * <br>
 * Prevents Endermen, Ravagers and Silverfish from destroying blocks
 * <br>
 * Prevents entities from trampling farmland
 */
@NullMarked
public class MobGriefingListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityExplode(EntityExplodeEvent event) {
        if(event.getEntityType() == EntityType.CREEPER || event.getEntityType() == EntityType.FIREBALL) {
            event.blockList().clear();
        }
    }

    private static final List<EntityType> blockChangingEntities = Arrays.asList(EntityType.ENDERMAN, EntityType.RAVAGER, EntityType.SILVERFISH);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(blockChangingEntities.contains(event.getEntityType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityFarmlandTrample(EntityInteractEvent event) {
        if(event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

}
