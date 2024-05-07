package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionEvents implements Listener {

    private final ScoreboardHandler scoreboardHandler;
    private final MemoryHandler memoryHandler;

    public ConnectionEvents(Main main) {
        this.scoreboardHandler = main.getScoreboardHandler();
        this.memoryHandler = main.getMemoryHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        scoreboardHandler.initPlayer(event.getPlayer());
        memoryHandler.addLoginTime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        memoryHandler.removeLoginTime(event.getPlayer());
    }

}
