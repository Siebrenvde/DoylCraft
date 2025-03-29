package dev.siebrenvde.doylcraft.player.preferences.menu;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.player.preferences.menu.option.MenuOption;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NullMarked
public abstract class PreferencesMenu {

    protected final Player player;
    protected final PlayerPreferences preferences;
    protected final InventoryView view;
    private final List<MenuOption> options = new ArrayList<>();

    protected PreferencesMenu(Player player, InventoryView view) {
        this.player = player;
        this.preferences = PlayerData.preferences(player);
        this.view = view;
        Bukkit.getPluginManager().registerEvents(new Listeners(), DoylCraft.instance());
    }

    protected void setOptions(MenuOption... options) {
        this.options.addAll(Arrays.asList(options));
        updateOptions();
    }

    protected MenuOption.Builder option(Material material, Component name) {
        return MenuOption.option(material, name, player);
    }

    private void updateOptions() {
        int i = 0;
        for (MenuOption option : options) {
            view.setItem(i, option.itemStack());
            i++;
        }
    }

    private void onClose() {
        if (this instanceof MainPreferencesMenu) return;
        Bukkit.getScheduler().runTask(DoylCraft.instance(), () -> MainPreferencesMenu.openMenu(player));
    }

    public InventoryView getView() {
        return view;
    }

    private class Listeners implements Listener {

        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getView() != view) return;
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) event.setCancelled(true);
            if (event.getClickedInventory() != view.getTopInventory()) return;
            event.setCancelled(true);
            int slot = event.getSlot();
            if (slot >= options.size()) return;
            if (options.get(slot).onClick()) updateOptions();
        }

        @EventHandler
        private void onInventoryDrag(InventoryDragEvent event) {
            if (event.getView() != view) return;
            if (event.getRawSlots().stream().noneMatch(i -> i < view.getTopInventory().getSize())) return;
            event.setCancelled(true);
        }

        @EventHandler
        private void onInventoryClose(InventoryCloseEvent event) {
            if (event.getView() != view) return;
            HandlerList.unregisterAll(this);
            if (event.getReason() == InventoryCloseEvent.Reason.PLAYER) onClose();
        }

    }

}
