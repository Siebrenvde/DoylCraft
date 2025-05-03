package dev.siebrenvde.doylcraft.player;

import dev.siebrenvde.configlib.ConfigLib;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.home.Homes;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.impl.util.ConfigsImpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class PlayerData {

    public static final Path DATA_PATH = DoylCraft.instance().getDataPath().resolve("player_data");

    private static final Map<UUID, PlayerPreferences> PREFERENCES = new HashMap<>();
    private static final Map<UUID, Homes> HOMES = new HashMap<>();
    private static final Map<UUID, Instant> LOGIN_TIMES = new HashMap<>();

    public static PlayerPreferences preferences(Player player) {
        return PREFERENCES.get(player.getUniqueId());
    }

    public static Homes homes(Player player) {
        return HOMES.get(player.getUniqueId());
    }

    public static Instant loginTime(Player player) {
        return LOGIN_TIMES.get(player.getUniqueId());
    }

    public static void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        loadPreferences(uuid);
        HOMES.put(uuid, new Homes(player));
        LOGIN_TIMES.put(uuid, Instant.now());
    }

    public static void deinitPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        unloadPreferences(uuid);
        HOMES.remove(player.getUniqueId());
        LOGIN_TIMES.remove(uuid);
    }

    private static void loadPreferences(UUID uuid) {
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

    @SuppressWarnings("unchecked")
    private static void unloadPreferences(UUID uuid) {
        PREFERENCES.remove(uuid).save();
        Map<String, ?> configs;
        try {
            Field field = ConfigsImpl.class.getDeclaredField("CONFIGS");
            field.setAccessible(true);
            configs = (Map<String, ?>) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        configs.remove(uuid.toString());
    }

    public static void createDir() throws IOException {
        Files.createDirectories(DATA_PATH);
    }

}
