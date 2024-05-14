package dev.siebrenvde.doylcraft.shops.events;

import dev.siebrenvde.doylcraft.shops.Shop;
import dev.siebrenvde.doylcraft.utils.Messages;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShopChestInteractEvent implements Listener {

    @EventHandler
    public void onShopChestInteract(PlayerInteractEvent event) {

        if(!event.getAction().isRightClick()) { return; }

        Block block = event.getClickedBlock();

        // If block is null or not a chest, return
        if(block == null || block.getType() != Material.CHEST) { return; }

        Chest chest = (Chest) block.getState();

        // If chest is not a shop, return
        if(!Shop.isShop(chest)) { return; }

        Player player = event.getPlayer();
        Shop shop = Shop.fromTileState(chest);

        // Fail-safe in case shop is somehow null
        if(shop == null) {
            event.setCancelled(true);
            player.sendMessage(Messages.error("Failed to retrieve shop, contact Siebrenvde."));
            return;
        }

        if(!shop.isOwner(player)) {
            event.setCancelled(true);
            // TODO: Open shop UI?
            player.sendMessage(Messages.error("You don't have permission to open this shop chest."));
        }

    }

}
