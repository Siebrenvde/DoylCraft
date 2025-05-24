package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.config.Config;
import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GameRuleListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onGameRuleChange(WorldGameRuleChangeEvent event) {
        if (event.getCommandSender() == null) return;
        if (!event.getGameRule().equals(GameRule.DO_DAYLIGHT_CYCLE)) return;
        Config.config().daylightCycle.setValue(Boolean.parseBoolean(event.getValue()));
    }

}
