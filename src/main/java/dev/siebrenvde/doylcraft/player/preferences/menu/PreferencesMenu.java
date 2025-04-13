package dev.siebrenvde.doylcraft.player.preferences.menu;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.menu.AbstractMenu;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.player.preferences.menu.option.MenuOption;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NullMarked
public abstract class PreferencesMenu extends AbstractMenu {

    protected final PlayerPreferences preferences;
    private final List<MenuOption> options = new ArrayList<>();

    protected PreferencesMenu(Player player, int slots, Component title) {
        super(player, slots , title);
        this.preferences = PlayerData.preferences(player);
    }

    protected void setOptions(MenuOption... options) {
        this.options.addAll(Arrays.asList(options));
        updateOptions();
    }

    protected MenuOption.Builder option(Material material, Component name) {
        return MenuOption.option(material, name, player);
    }

    private void updateOptions() {
        inventory.clear();
        fillInventory(options.stream().map(MenuOption::itemStack).toList());
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot >= options.size()) return;
        if (options.get(slot).onClick()) updateOptions();
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (
            event.getReason() == InventoryCloseEvent.Reason.PLAYER
            && !(this instanceof MainPreferencesMenu)
        ) {
            Bukkit.getScheduler().runTask(DoylCraft.instance(), () -> MainPreferencesMenu.openMenu(player));
        }
    }

}
