package dev.siebrenvde.doylcraft.warp;

import dev.siebrenvde.doylcraft.location.LocationListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class WarpMenu extends LocationListMenu<Warp> {

    private static final Component TITLE = Component.text("Warps");

    private WarpMenu(Player player, List<Warp> locations) {
        super(player, locations);
    }

    public static void open(Player player) {
        new WarpMenu(player, Warps.WARPS.values().stream().toList());
    }

    @Override
    protected Component title() {
        return TITLE;
    }

    @Override
    protected String command(Warp warp) {
        return "warp " + warp.key();
    }

}
