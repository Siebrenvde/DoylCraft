package dev.siebrenvde.doylcraft.preferences.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class PetDamageMessagesPreferencesMenu extends PreferencesMenu {

    private PetDamageMessagesPreferencesMenu(Player player) {
        super(
            player,
            MenuType.GENERIC_9X1.builder()
                .title(text("Pet Damage Messages"))
                .build(player)
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
        new PetDamageMessagesPreferencesMenu(player).getView().open();
    }

}
