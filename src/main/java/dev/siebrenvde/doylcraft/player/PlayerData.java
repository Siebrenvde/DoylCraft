package dev.siebrenvde.doylcraft.player;

import dev.siebrenvde.configlib.ConfigLib;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class PlayerData {

    private static final Path DATA_PATH = DoylCraft.instance().getDataPath().resolve("player_data");

    private static final Map<UUID, PlayerPreferences> PREFERENCES = new HashMap<>();

    public static PlayerPreferences preferences(Player player) {
        return PREFERENCES.get(player.getUniqueId());
    }

    public static void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!PREFERENCES.containsKey(uuid)) {
            PREFERENCES.put(
                uuid,
                ConfigLib.json(
                    DATA_PATH,
                    uuid.toString(),
                    "preferences",
                    PlayerPreferences.class
                )
            );
        }
    }

    public static void createDir() throws IOException {
        Files.createDirectories(DATA_PATH);
    }

}
