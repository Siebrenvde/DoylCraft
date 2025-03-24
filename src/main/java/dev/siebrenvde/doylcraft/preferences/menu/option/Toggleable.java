package dev.siebrenvde.doylcraft.preferences.menu.option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import org.quiltmc.config.api.values.TrackedValue;

import static net.kyori.adventure.text.Component.text;

public class Toggleable extends MenuOption {

    private final TrackedValue<Boolean> preference;

    Toggleable(Material material, Component name, @Nullable Component description, Player player, TrackedValue<Boolean> preference) {
        super(material, name, description, text("Click to toggle", NamedTextColor.AQUA), player);
        this.preference = preference;
        updateLore();
    }

    @Override
    public boolean onClick() {
        preference.setValue(!preference.getRealValue());
        updateLore();
        return true;
    }

    private void updateLore() {
        setLore(
            preference.getRealValue()
                ? text("Enabled", NamedTextColor.GREEN)
                : text("Disabled", NamedTextColor.RED)
        );
    }

}
