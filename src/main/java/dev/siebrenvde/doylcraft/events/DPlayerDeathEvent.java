package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DPlayerDeathEvent implements Listener {

    private ScoreboardHandler handler;

    public DPlayerDeathEvent(ScoreboardHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        handler.updatePlayer(player);

    }

}
