package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DPlayerJoinEvent implements Listener {

    private Main main;
    private ScoreboardHandler handler;

    public DPlayerJoinEvent(Main main) {
        this.main = main;
        handler = main.getScoreboardHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        handler.showScoreboard(player);
        handler.updatePlayer(player);

    }

}
