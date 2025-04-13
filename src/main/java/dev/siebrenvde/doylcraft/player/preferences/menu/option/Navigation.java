package dev.siebrenvde.doylcraft.player.preferences.menu.option;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.preferences.menu.PreferencesMenu;
import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

import static net.kyori.adventure.text.Component.text;

public class Navigation extends MenuOption {

    private final Class<? extends PreferencesMenu> menuClass;

    Navigation(Material material, Component name, @Nullable Component description, Player player, Class<? extends PreferencesMenu> menuClass) {
        super(material, name, description, text("Click to open category", NamedTextColor.AQUA), player);
        this.menuClass = menuClass;
    }

    @Override
    public boolean onClick() {
        PreferencesMenu menu;

        try {
            Constructor<? extends PreferencesMenu> constructor = menuClass.getDeclaredConstructor(Player.class);
            constructor.setAccessible(true);
            menu = constructor.newInstance(player);
        } catch (ReflectiveOperationException e) {
            player.sendMessage(Components.exception(
                text("Failed to open menu " + menuClass.getSimpleName()),
                e
            ));
            DoylCraft.logger().error("Failed to open menu {} for player {}", menuClass.getSimpleName(), player.getName());
            DoylCraft.logger().error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }

        player.closeInventory();
        Bukkit.getScheduler().runTask(DoylCraft.instance(), menu::open);
        return false;
    }

}
