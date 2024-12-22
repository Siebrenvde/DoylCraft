package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.handlers.MemoryHandler;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.addons.VoicechatAddon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.stream.Collectors;

public class ConnectionEvents implements Listener {

    private final ScoreboardHandler scoreboardHandler;
    private final MemoryHandler memoryHandler;
    private final DiscordSRVAddon discordSRVAddon;

    public ConnectionEvents(DoylCraft doylCraft) {
        this.scoreboardHandler = doylCraft.getScoreboardHandler();
        this.memoryHandler = doylCraft.getMemoryHandler();
        this.discordSRVAddon = doylCraft.getDiscordSRVAddon();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        scoreboardHandler.initPlayer(player);
        memoryHandler.addLoginTime(player);
        VoicechatAddon.checkVoicechatInstalled(player);
        player.addCustomChatCompletions(
            discordSRVAddon.getMembers().stream()
                .map(m -> "@" + m.getEffectiveName())
                .collect(Collectors.toList())
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        memoryHandler.removeLoginTime(event.getPlayer());
    }

}
