package dev.siebrenvde.doylcraft.shops.events;

import dev.siebrenvde.doylcraft.shops.Shop;
import dev.siebrenvde.doylcraft.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ShopCreateEvent implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        Block block = event.getBlock();

        // No hanging signs or sign posts
        if(!(block.getBlockData() instanceof WallSign wallSign)) { return; }

        boolean isShopSign = false; // TODO: Check if lines is empty when no text, can't currently test

        // Sign should have [shop] on any line and no other text
        for(Component lineComponent : event.lines()) {
            if(lineComponent == null) { continue; } // If line is empty, move to next line
            String lineText = ((TextComponent) lineComponent).content();
            if(!lineText.equalsIgnoreCase("[shop]")) { return; } // If line does not contain [shop], return
            isShopSign = true; // If line contains [shop], sign is shop sign
        }

        // Return if sign is empty
        if(!isShopSign) { return; }

        Sign sign = (Sign) block.getState();

        // Gets the block the sign is attached to
        Block attachedBlock = block.getRelative(wallSign.getFacing().getOppositeFace());

        Player player = event.getPlayer();

        if(!(attachedBlock.getBlockData() instanceof Chest chestData)) {
            player.sendMessage(Messages.error("A shop sign must be placed on a chest."));
            return;
        }

        org.bukkit.block.Chest mainChest = (org.bukkit.block.Chest) attachedBlock.getState();
        org.bukkit.block.Chest secondaryChest = null;

        // If double chest, set secondary chest
        if(mainChest.getInventory().getHolder() instanceof DoubleChest doubleChest) {
            secondaryChest =
                    chestData.getType() == Chest.Type.LEFT
                    ? (org.bukkit.block.Chest) doubleChest.getLeftSide()
                    : (org.bukkit.block.Chest) doubleChest.getRightSide();
        }

        if(Shop.isShop(mainChest)) {
            player.sendMessage(Messages.error("This chest is already a shop."));
            return;
        }

        Shop shop = new Shop(
                player,
                null,
                sign,
                mainChest,
                secondaryChest
        );

        // Add shop data to sign and chests
        shop.update();

        // TODO: Open management UI

    }

}
