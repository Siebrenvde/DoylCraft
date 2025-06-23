package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.player.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.Map;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.title.Title.Times.times;
import static net.kyori.adventure.title.Title.title;

@NullMarked
public class PlayerChangedWorldListener implements Listener {

    private static final Duration FADE_IN_DURATION = Duration.ofMillis(500);
    private static final Duration STAY_DURATION = Duration.ofMillis(1500);
    private static final Duration FADE_OUT_DURATION = Duration.ofMillis(500);

    private static final Title.Times TITLE_TIMES = times(
        FADE_IN_DURATION,
        STAY_DURATION,
        FADE_OUT_DURATION
    );

    private static final Map<Key, Component> DIMENSION_NAMES = Map.of(
        key("overworld"), text("The Overworld"),
        key("the_nether"), text("The Nether"),
        key("the_end"), text("The End")
    );

    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!PlayerData.preferences(player).showDimensionTitles()) return;

        World world = player.getWorld();
        Component name = DIMENSION_NAMES.get(world.key());
        if (name == null) return;

        player.showTitle(title(name, createSubTitle(world), TITLE_TIMES));
    }

    private Component createSubTitle(World world) {
        return text()
            .append(text("Keep Inventory is "))
            .append(
                Boolean.TRUE.equals(world.getGameRuleValue(GameRule.KEEP_INVENTORY))
                    ? text("enabled", NamedTextColor.GREEN)
                    : text("disabled", NamedTextColor.RED)
            )
            .build();
    }

}
