package dev.siebrenvde.doylcraft.preferences;

import dev.siebrenvde.configlib.ConfigLib;
import dev.siebrenvde.doylcraft.DoylCraft;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class Preferences {

    private static final Map<UUID, PlayerPreferences> PREFERENCES = new HashMap<>();

    public static PlayerPreferences get(Player player) {
        return PREFERENCES.get(player.getUniqueId());
    }

    public static void initPlayer(Player player) {
        if(!PREFERENCES.containsKey(player.getUniqueId())) {
            PREFERENCES.put(
                player.getUniqueId(),
                ConfigLib.json(
                    DoylCraft.instance().getDataPath(),
                    "preferences",
                    player.getUniqueId().toString(),
                    PlayerPreferences.class
                )
            );
        }
    }

}
