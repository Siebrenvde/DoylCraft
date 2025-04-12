package dev.siebrenvde.doylcraft.player;

import dev.siebrenvde.configlib.ConfigLib;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.home.Homes;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class PlayerData {

    public static final Path DATA_PATH = DoylCraft.instance().getDataPath().resolve("player_data");

    private static final Map<UUID, PlayerPreferences> PREFERENCES = new HashMap<>();
    private static final Map<UUID, Homes> HOMES = new HashMap<>();

    public static PlayerPreferences preferences(Player player) {
        return PREFERENCES.get(player.getUniqueId());
    }

    public static Homes homes(Player player) {
        return HOMES.get(player.getUniqueId());
    }

    public static @Nullable Homes homes(UUID uuid) {
        return HOMES.get(uuid);
    }

    public static void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        loadPreferences(uuid);
        HOMES.put(uuid, new Homes(player));
    }

    public static void deinitPlayer(Player player) {
        HOMES.remove(player.getUniqueId());
    }

    private static void loadPreferences(UUID uuid) {
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
