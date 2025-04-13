package dev.siebrenvde.doylcraft.player.home;

import dev.siebrenvde.doylcraft.location.LocationListMenu;
import dev.siebrenvde.doylcraft.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class HomeMenu extends LocationListMenu<Home> {

    private static final Component TITLE = Component.text("Homes");

    private HomeMenu(Player player, List<Home> homes) {
        super(player, homes, TITLE);
    }

    public static void tryOpen(Player player) {
        List<Home> homes = PlayerData.homes(player).asList();
        if (homes.isEmpty()) {
            player.sendMessage(Component.text("You don't have any homes", NamedTextColor.RED));
            return;
        }
        new HomeMenu(player, homes).open();
    }

    @Override
    protected String command(Home home) {
        return "home " + home.key();
    }

}
