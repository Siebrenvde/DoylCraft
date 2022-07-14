package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionEvents implements Listener {

    private Main main;
    private ScoreboardHandler handler;
    private TimeHandler time;

    public ConnectionEvents(Main main) {
        this.main = main;
        handler = main.getScoreboardHandler();
        time = main.getTimeHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        handler.initPlayer(event.getPlayer());
        time.addLoginTime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        time.removeLoginTime(event.getPlayer());
        handler.removePlayer(event.getPlayer());
    }

}
