package dev.siebrenvde.doylcraft.player.preferences.menu;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public class MainPreferencesMenu extends PreferencesMenu implements Listener {

    private MainPreferencesMenu(Player player) {
        super(
            player,
            9, // Temporarily hardcoded
            text("Preferences")
        );

        setOptions(
            option(Material.CLOCK, text("Time Zone"))
                .description(text("The time zone used in the playtime command"))
                .commandInput(text(preferences.timezone.getRealValue()), "timezone"),
            option(Material.NOTE_BLOCK, text("Voicechat Reminder"))
                .description(text("The reminder received on login if you don't have Simple Voice Chat installed"))
                .toggleable(preferences.voicechatReminder),
            option(randomMaterial(MaterialTags.BEDS), text("Replace Homes"))
                .description(text("Whether to automatically replace an existing home when using /sethome"))
                .toggleable(preferences.replaceHomes),
            option(Material.ENDER_PEARL, text("Use Teleportation Menus"))
                .description(text("Whether to open a menu for the home and warp commands"))
                .toggleable(preferences.useTeleportMenus),
            option(Material.END_PORTAL_FRAME, text("Show Dimension Titles"))
                .description(text("Show a title when switching dimensions"))
                .toggleable(preferences.showDimensionTitles),
            option(randomMaterial(MaterialTags.WOODEN_DOORS), text("Open Double Doors"))
                .description(text("Open both double doors when opening one"))
                .toggleable(preferences.openDoubleDoors),
            option(Material.BELL, text("Durability Ping"))
                .description(text("Receive a warning when your tools are low on durability"))
                .navigation(DurabilityPingPreferencesMenu.class),
            option(Material.BONE, text("Pet Damage Messages"))
                .description(text("Receive a message when someone harms one of your pets"))
                .navigation(PetDamageMessagesPreferencesMenu.class)
        );
    }

    private Material randomMaterial(MaterialSetTag tag) {
        Set<Material> materials = tag.getValues();
        return materials.toArray(Material[]::new)[new Random().nextInt(materials.size())];
    }

    public static void openMenu(Player player) {
        new MainPreferencesMenu(player).open();
    }

}
