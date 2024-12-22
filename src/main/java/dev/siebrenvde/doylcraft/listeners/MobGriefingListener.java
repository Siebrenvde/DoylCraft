package dev.siebrenvde.doylcraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.Arrays;
import java.util.List;

public class MobGriefingListener implements Listener {

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        if(event.getEntityType() == EntityType.CREEPER || event.getEntityType() == EntityType.FIREBALL) {
            event.blockList().clear();
        }
    }

    private static final List<EntityType> blockChangingEntities = Arrays.asList(EntityType.ENDERMAN, EntityType.RAVAGER, EntityType.SILVERFISH);

    @EventHandler
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(blockChangingEntities.contains(event.getEntityType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityFarmlandTrample(EntityInteractEvent event) {
        if(event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

}
