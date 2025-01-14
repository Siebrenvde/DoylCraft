package dev.siebrenvde.doylcraft.handlers;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.commands.SilenceCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryHandler {

    /**
     * The list of players who executed {@link dev.siebrenvde.doylcraft.commands.GetOwnerCommand}
     * and have not yet interacted with an entity
     */
    public static final List<Player> GET_OWNER_PLAYERS = new ArrayList<>();

    /**
     * The list of players who executed {@link SilenceCommand}
     * and have not yet interacted with an entity
     */
    public static final Map<Player, SilenceCommand.CommandType> SILENCE_PLAYERS = new HashMap<>();

    /**
     * The list of login times for players
     */
    public static final HashMap<Player, Instant> LOGIN_TIMES = new HashMap<>();

    /**
     * Removes the player from {@link MemoryHandler#GET_OWNER_PLAYERS} after 10 seconds
     * @param player the player
     */
    public static void startGetOwnerCountdown(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(
            DoylCraft.getInstance(),
            () -> GET_OWNER_PLAYERS.remove(player),
            200L
        );
    }

    /**
     * Removes the player from {@link MemoryHandler#SILENCE_PLAYERS} after 10 seconds
     * @param player the player
     */
    public static void startSilenceCountdown(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(
            DoylCraft.getInstance(),
            () -> SILENCE_PLAYERS.remove(player),
            200L
        );
    }

}
