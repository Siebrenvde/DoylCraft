package dev.siebrenvde.doylcraft.preferences.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class MainPreferencesMenu extends PreferencesMenu implements Listener {

    private MainPreferencesMenu(Player player) {
        super(
            player,
            MenuType.GENERIC_9X1.builder()
                .title(text("Preferences"))
                .build(player)
        );

        setOptions(
            option(Material.CLOCK, text("Time Zone"))
                .description(text("The time zone used in the playtime command"))
                .commandInput(text(preferences.timezone.getRealValue()), "timezone"),
            option(Material.NOTE_BLOCK, text("Voicechat Reminder"))
                .description(text("The reminder received on login if you don't have Simple Voice Chat installed"))
                .toggleable(preferences.voicechatReminder),
            option(Material.BELL, text("Durability Ping"))
                .description(text("Receive a warning when your tools are low on durability"))
                .navigation(DurabilityPingPreferencesMenu.class),
            option(Material.BONE, text("Pet Damage Messages"))
                .description(text("Receive a message when someone harms one of your pets"))
                .navigation(PetDamageMessagesPreferencesMenu.class)
        );
    }

    public static void openMenu(Player player) {
        new MainPreferencesMenu(player).getView().open();
    }

}
