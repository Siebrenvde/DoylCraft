package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DPlayerDeathEvent implements Listener {

    private ScoreboardHandler scoreboardHandler;

    public DPlayerDeathEvent(ScoreboardHandler scoreboardHandler) {
        this.scoreboardHandler = scoreboardHandler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        scoreboardHandler.updatePlayer(player);

    }

}
