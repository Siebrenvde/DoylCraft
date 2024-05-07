package dev.siebrenvde.doylcraft.handlers;

import dev.siebrenvde.doylcraft.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MemoryHandler {

    // List of players who executed /getowner and have not yet interacted with an entity
    private final List<Player> getOwnerPlayers = new ArrayList<>();

    public MemoryHandler() {}

    public boolean getOwnerListContains(Player player) { return getOwnerPlayers.contains(player); }
    public void addGetOwnerPlayer(Player player) { getOwnerPlayers.add(player); }
    public void removeGetOwnerPlayer(Player player) { getOwnerPlayers.remove(player); }
    // Removes player from getOwnerPlayers after 5 seconds
    public void startGetOwnerCountdown(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getOwnerListContains(player)) {
                    removeGetOwnerPlayer(player);
                }
            }
        }.runTaskLaterAsynchronously(Main.getInstance(), 200L);
    }

}
