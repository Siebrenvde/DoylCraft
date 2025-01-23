package dev.siebrenvde.doylcraft.preferences;

import dev.siebrenvde.configlib.ConfigLib;
import dev.siebrenvde.doylcraft.DoylCraft;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Preferences {

    private static final Map<UUID, PlayerPreferences> PREFERENCES = new HashMap<>();

    public static PlayerPreferences get(Player player) {
        return PREFERENCES.get(player.getUniqueId());
    }

    public static void initPlayer(Player player) {
        if(!PREFERENCES.containsKey(player.getUniqueId())) {
            PREFERENCES.put(
                player.getUniqueId(),
                ConfigLib.toml(
                    DoylCraft.getInstance().getDataPath(),
                    "preferences",
                    player.getUniqueId().toString(),
                    PlayerPreferences.class
                )
            );
        }
    }

}
