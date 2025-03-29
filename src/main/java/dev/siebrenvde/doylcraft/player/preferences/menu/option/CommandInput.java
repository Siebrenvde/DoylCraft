package dev.siebrenvde.doylcraft.player.preferences.menu.option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

public class CommandInput extends MenuOption {

    private final String command;

    CommandInput(
        Material material,
        Component name,
        @Nullable Component description,
        Player player,
        Component valueDisplay,
        String command
    ) {
        super(material, name, description, text("Click to change", NamedTextColor.AQUA), player);
        this.command = command;
        setLore(valueDisplay.colorIfAbsent(NamedTextColor.YELLOW));
    }

    @Override
    public boolean onClick() {
        player.closeInventory();
        player.sendMessage(
            text()
                .append(text("Click this message to autofill the command"))
                .clickEvent(ClickEvent.suggestCommand("/doylcraft preferences " + command + " "))
        );
        return false;
    }

}
