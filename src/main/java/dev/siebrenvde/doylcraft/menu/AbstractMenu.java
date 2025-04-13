package dev.siebrenvde.doylcraft.menu;

import dev.siebrenvde.doylcraft.DoylCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public abstract class AbstractMenu implements InventoryHolder {

    protected final Player player;
    protected final Inventory inventory;

    protected AbstractMenu(Player player, int slots, Component title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(
            this,
            9 * (int) Math.ceil((double) slots / 9),
            title
        );
        Bukkit.getPluginManager().registerEvents(new Listeners(), DoylCraft.instance());
    }

    protected void fillInventory(List<ItemStack> stacks) {
        int i = 0;
        for (ItemStack stack : stacks) {
            inventory.setItem(i++, stack);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        player.openInventory(inventory);
    }

    protected void onClick(InventoryClickEvent event) {

    }

    protected void onClose(InventoryCloseEvent event) {

    }

    private class Listeners implements Listener {

        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory() != inventory) return;
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) event.setCancelled(true);
            if (event.getClickedInventory() != inventory) return;
            event.setCancelled(true);
            onClick(event);
        }

        @EventHandler
        private void onInventoryDrag(InventoryDragEvent event) {
            if (event.getInventory() != inventory) return;
            if (event.getRawSlots().stream().noneMatch(i -> i < inventory.getSize())) return;
            event.setCancelled(true);
        }

        @EventHandler
        private void onInventoryClose(InventoryCloseEvent event) {
            if (event.getInventory() != inventory) return;
            HandlerList.unregisterAll(this);
            onClose(event);
        }

    }

}
