package dev.siebrenvde.doylcraft.utils;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.handlers.TimeHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ReloadHandler {

    Main main;
    TimeHandler time;
    ScoreboardHandler scoreboard;

    private File fFile;
    private FileConfiguration file;

    public ReloadHandler(Main main) {
        this.main = main;
        time = main.getTimeHandler();
        scoreboard = main.getScoreboardHandler();
        fFile = new File(main.getDataFolder(), "data.yml");
    }

    public void loadData() {

        if(!fFile.exists()) {
            main.getLogger().severe("Failed to load reload data!");
        }
        file = YamlConfiguration.loadConfiguration(fFile);

        if(Bukkit.getOnlinePlayers().size() > 0) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                scoreboard.initPlayer(p);
                time.setLoginTime(p, file.getLong("login-times." + p.getUniqueId().toString()));
            }
        }

    }

    public void saveData() {
        main.saveResource("data.yml", true);
        file = YamlConfiguration.loadConfiguration(fFile);

        file.createSection("login-times");

        for(Player p : Bukkit.getOnlinePlayers()) {
            file.set("login-times." + p.getUniqueId().toString(), time.getLoginTime(p));
        }

        try {
            file.save(fFile);
        } catch (IOException e) {
            main.getLogger().severe("Failed to save reload data!");
            e.printStackTrace();
        }
    }

}
