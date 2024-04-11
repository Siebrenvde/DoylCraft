package dev.siebrenvde.doylcraft.shops.events;

import dev.siebrenvde.doylcraft.shops.Shop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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

        // TODO: Allow on any line
        if(event.line(0) == null) { return; }

        String lineContent = ((TextComponent)event.line(0)).content();

        if(!lineContent.equalsIgnoreCase("[shop]")) { return; }

        Sign sign = (Sign) block.getState();

        // Gets the block the sign is attached to
        Block attachedBlock = block.getRelative(wallSign.getFacing().getOppositeFace());

        Player player = event.getPlayer();

        if(!(attachedBlock.getBlockData() instanceof Chest chestData)) {
            TextComponent tc = Component.text("A shop sign must be placed on a chest.").color(NamedTextColor.RED);
            player.sendMessage(tc);
            return;
        }

        org.bukkit.block.Chest mainChest = (org.bukkit.block.Chest) attachedBlock.getState();
        org.bukkit.block.Chest secondaryChest = null;

        // If double chest, set secondary chest
        if(mainChest.getInventory().getHolder() instanceof  DoubleChest doubleChest) {
            secondaryChest =
                    chestData.getType() == Chest.Type.LEFT
                    ? (org.bukkit.block.Chest) doubleChest.getLeftSide()
                    : (org.bukkit.block.Chest) doubleChest.getRightSide();
        }

        // TODO: Check if already shop

        Shop shop = new Shop(
                player,
                null,
                null,
                sign,
                mainChest,
                secondaryChest
        );

    }

}
