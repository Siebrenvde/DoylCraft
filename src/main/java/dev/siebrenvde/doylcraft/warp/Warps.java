package dev.siebrenvde.doylcraft.warp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.siebrenvde.doylcraft.DoylCraft;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.siebrenvde.doylcraft.DoylCraft.logger;

@NullMarked
public class Warps {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path PATH = DoylCraft.instance().getDataPath().resolve("warps.json");

    public static final Map<String, Warp> WARPS = new HashMap<>();

    /**
     * Attempts to load the warps from <code>warps.json</code> into {@link Warps#WARPS}
     */
    public static void loadWarps(boolean log) {
        JsonArray array;
        try {
            BufferedReader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8);
            array = GSON.fromJson(reader, JsonArray.class);
        } catch (NoSuchFileException ignored) {
            if (log) logger().info("No warps loaded");
            return;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load warps", e);
        }
        Warp.CODEC.listOf().parse(JsonOps.INSTANCE, array)
            .getOrThrow(RuntimeException::new)
            .forEach(warp -> {
                WARPS.put(warp.key(), warp);
                if (log) logger().info("Loaded warp '{}'", warp.key());
            });
        int amount = WARPS.size();
        if (log) logger().info("Loaded {} warp{}", amount, amount != 1 ? "s" : "");
    }

    /**
     * Attempts to save the warps in {@link Warps#WARPS} into <code>warps.json</code>
     */
    public static void saveWarps() {
        JsonElement warps = Warp.CODEC.listOf()
            .encodeStart(JsonOps.INSTANCE, List.copyOf(WARPS.values()))
            .getOrThrow(RuntimeException::new);
        try {
            Files.createDirectories(PATH.getParent());
            BufferedWriter writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8);
            GSON.toJson(warps, writer);
            writer.write('\n');
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warps", e);
        }
    }

}
