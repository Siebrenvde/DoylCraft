package dev.siebrenvde.doylcraft.player.preferences.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public class PetDamageMessagesPreferencesMenu extends PreferencesMenu {

    private PetDamageMessagesPreferencesMenu(Player player) {
        super(
            player,
            9, // Temporarily hardcoded
            text("Pet Damage Messages")
        );

        setOptions(
            option(Material.GOAT_HORN, text("Broadcasts"))
                .description(text("The global broadcast sent when a pet is killed"))
                .toggleable(preferences.petDamageMessages.broadcast),
            option(Material.LEAD, text("Owner Messages"))
                .description(text("The message you receive when your pet is attacked by another player"))
                .toggleable(preferences.petDamageMessages.owner),
            option(Material.IRON_SWORD, text("Attacker Messages"))
                .description(text("The message you receive when you attack a pet (including your own)"))
                .toggleable(preferences.petDamageMessages.attacker)
        );
    }

    public static void openMenu(Player player) {
        new PetDamageMessagesPreferencesMenu(player).open();
    }

}
