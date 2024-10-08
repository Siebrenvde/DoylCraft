package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.handlers.VoicechatHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionEvents implements Listener {

    private final ScoreboardHandler scoreboardHandler;
    private final MemoryHandler memoryHandler;

    public ConnectionEvents(DoylCraft doylCraft) {
        this.scoreboardHandler = doylCraft.getScoreboardHandler();
        this.memoryHandler = doylCraft.getMemoryHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        scoreboardHandler.initPlayer(event.getPlayer());
        memoryHandler.addLoginTime(event.getPlayer());
        VoicechatHandler.checkVoicechatInstalled(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        memoryHandler.removeLoginTime(event.getPlayer());
    }

}
