package dev.siebrenvde.doylcraft.handlers;

import dev.siebrenvde.doylcraft.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ReloadHandler {

    private final Main main;
    private final MemoryHandler memoryHandler;
    private final ScoreboardHandler scoreboardHandler;

    private final File fFile;
    private FileConfiguration file;

    public ReloadHandler(Main main) {
        this.main = main;
        memoryHandler = main.getMemoryHandler();
        scoreboardHandler = main.getScoreboardHandler();
        fFile = new File(main.getDataFolder(), "data.yml");
    }

    public void loadData() {

        if(!fFile.exists()) {
            main.getLogger().severe("Failed to load reload data!");
        }
        file = YamlConfiguration.loadConfiguration(fFile);

        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                scoreboardHandler.initPlayer(p);
                memoryHandler.addLoginTime(p, file.getLong("login-times." + p.getUniqueId()));
            }
        }

    }

    public void saveData() {
        main.saveResource("data.yml", true);
        file = YamlConfiguration.loadConfiguration(fFile);

        file.createSection("login-times");

        for(Player p : Bukkit.getOnlinePlayers()) {
            file.set("login-times." + p.getUniqueId().toString(), memoryHandler.getLoginTime(p));
        }

        try {
            file.save(fFile);
        } catch (IOException e) {
            main.getLogger().severe("Failed to save reload data!");
            e.printStackTrace();
        }
    }

}
