package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.addons.VoicechatAddon;
import dev.siebrenvde.doylcraft.preferences.Preferences;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Listener for {@link PlayerJoinEvent} and {@link PlayerQuitEvent}
 * <p>
 * Initialises scoreboard data and login time, checks for Simple Voice Chat and sends Discord member completions
 */
public class ConnectionListener implements Listener {

    private final ScoreboardHandler scoreboardHandler;
    private final DiscordSRVAddon discordSRVAddon;

    public ConnectionListener(DoylCraft doylCraft) {
        this.scoreboardHandler = doylCraft.getScoreboardHandler();
        this.discordSRVAddon = doylCraft.getDiscordSRVAddon();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Preferences.initPlayer(player);
        scoreboardHandler.initPlayer(player);
        MemoryHandler.LOGIN_TIMES.put(player, Instant.now());
        VoicechatAddon.checkVoicechatInstalled(player);
        player.addCustomChatCompletions(
            discordSRVAddon.getMembers().stream()
                .map(m -> "@" + m.getEffectiveName())
                .collect(Collectors.toList())
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MemoryHandler.LOGIN_TIMES.remove(event.getPlayer());
    }

}
