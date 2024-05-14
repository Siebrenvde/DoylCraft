package dev.siebrenvde.doylcraft.shops.events;

import dev.siebrenvde.doylcraft.shops.Shop;
import dev.siebrenvde.doylcraft.utils.Messages;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class ShopBreakEvent implements Listener {

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {

        Block block = event.getBlock();

        // Return if block is not a chest or sign
        if(!(block.getBlockData() instanceof Chest) && !(block.getBlockData() instanceof WallSign)) { return; }

        TileState tileState = (TileState) block.getState();

        // Return if block is not a shop
        if(!Shop.isShop(tileState)) { return; }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Shop shop = Shop.fromTileState(tileState);

        // Fail-safe in case shop is somehow null
        if(shop == null) {
            player.sendMessage(Messages.error("Failed to retrieve shop, contact Siebrenvde."));
            return;
        }

        if(!shop.isOwner(player)) {
            player.sendMessage(Messages.error("You don't have permission to break this shop."));
            return;
        }

        // TODO: Remove, temporary
        if(player.isSneaking()) {
            event.setCancelled(false);
            shop.destroyTesting();
            return;
        }

        // If player is shop owner
        // TODO: Replace <command> with shop delete command
        player.sendMessage(Messages.error("Use <command> to delete the shop before breaking it."));

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeAll(getShopBlocks(event.blockList()));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeAll(getShopBlocks(event.blockList()));
    }

    private List<Block> getShopBlocks(List<Block> blockList) {
        List<Block> shopBlocks = new ArrayList<>();
        for(Block block : blockList) {
            if(!(block.getBlockData() instanceof Chest) && !(block.getBlockData() instanceof WallSign)) { continue; }
            TileState tileState = (TileState) block.getState();
            if(!Shop.isShop(tileState)) { continue; }
            shopBlocks.add(block);
        }
        return shopBlocks;
    }

}
