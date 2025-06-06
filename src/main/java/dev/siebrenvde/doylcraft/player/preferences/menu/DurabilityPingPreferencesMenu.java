package dev.siebrenvde.doylcraft.player.preferences.menu;

import dev.siebrenvde.doylcraft.utils.Components;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public class DurabilityPingPreferencesMenu extends PreferencesMenu {

    private DurabilityPingPreferencesMenu(Player player) {
        super(
            player,
            9, // Temporarily hardcoded
            text("Durability Ping")
        );

        setOptions(
            option(Material.BELL, text("Durability Ping"))
                .toggleable(preferences.durabilityPing.enabled),
            option(Material.COMPARATOR, text("Percentage"))
                .description(text("The percentage of remaining durability at which you start receiving pings"))
                .commandInput(
                    text(preferences.durabilityPing.percentage() + "%"),
                    "durability_ping percentage"
                ),
            option(Material.CLOCK, text("Cooldown"))
                .description(text("The cooldown between pings"))
                .commandInput(
                    Components.duration(Duration.ofSeconds(preferences.durabilityPing.cooldown())),
                    "durability_ping cooldown"
                )
        );
    }

    public static void openMenu(Player player) {
        new DurabilityPingPreferencesMenu(player).open();
    }

}
