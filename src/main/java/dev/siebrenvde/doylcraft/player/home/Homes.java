package dev.siebrenvde.doylcraft.player.home;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.siebrenvde.doylcraft.utils.Components;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static dev.siebrenvde.doylcraft.DoylCraft.GSON;
import static dev.siebrenvde.doylcraft.player.PlayerData.DATA_PATH;
import static net.kyori.adventure.text.Component.text;

@NullMarked
public final class Homes implements Iterable<Home> {

    private final Path path;

    private final Player player;
    private final Map<String, Home> homes = new HashMap<>();

    public Homes(Player player) {
        this.path = DATA_PATH.resolve(player.getUniqueId().toString()).resolve("homes.json");
        this.player = player;
        load();
    }

    public boolean has(String key) {
        return homes.containsKey(key);
    }

    public @Nullable Home get(String key) {
        return homes.get(key);
    }

    public Home getOrThrow(String key) {
        Home home = homes.get(key);
        if (home == null) throw new IllegalStateException("Home '" + key + "' does not exist");
        return home;
    }

    public void add(Home home) {
        homes.put(home.key(), home);
    }

    public @Nullable Home addIfAbsent(Home home) {
        return homes.putIfAbsent(home.key(), home);
    }

    public void remove(Home home) {
        homes.remove(home.key());
    }

    public int size() {
        return homes.size();
    }

    public List<Home> asList() {
        return homes.values().stream().toList();
    }

    @Override
    public Iterator<Home> iterator() {
        return homes.values().iterator();
    }

    public void load() {
        JsonArray array;
        try {
            BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            array = GSON.fromJson(reader, JsonArray.class);
        } catch (NoSuchFileException ignored) {
            return;
        } catch (IOException e) {
            if (player.isOnline()) player.sendMessage(Components.exception(text("Failed to load homes"), e));
            throw new RuntimeException("Failed to load homes for " + player.getName(), e);
        }
        Home.CODEC.listOf().parse(JsonOps.INSTANCE, array)
            .getOrThrow(message -> {
                RuntimeException exception = new RuntimeException(message);
                if (player.isOnline()) player.sendMessage(Components.exception(text("Failed to parse homes"), exception));
                return exception;
            })
            .forEach(home -> homes.put(home.key(), home));
    }

    public void save() {
        JsonElement jsonHomes = Home.CODEC.listOf()
            .encodeStart(JsonOps.INSTANCE, List.copyOf(homes.values()))
            .getOrThrow(RuntimeException::new);
        try {
            Files.createDirectories(path.getParent());
            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            GSON.toJson(jsonHomes, writer);
            writer.write('\n');
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save homes for " + player.getName(), e);
        }
    }

}
