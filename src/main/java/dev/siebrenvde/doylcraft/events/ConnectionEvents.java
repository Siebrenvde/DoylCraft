package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionEvents implements Listener {

    private ScoreboardHandler scoreboardHandler;
    private TimeHandler timeHandler;

    public ConnectionEvents(Main main) {
        this.scoreboardHandler = main.getScoreboardHandler();
        this.timeHandler = main.getTimeHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        scoreboardHandler.initPlayer(event.getPlayer());
        timeHandler.addLoginTime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        timeHandler.removeLoginTime(event.getPlayer());
        scoreboardHandler.removePlayer(event.getPlayer());
    }

}
