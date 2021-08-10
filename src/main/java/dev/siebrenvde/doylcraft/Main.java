package dev.siebrenvde.doylcraft;

import dev.siebrenvde.doylcraft.commands.PvP;
import dev.siebrenvde.doylcraft.commands.Rank;
import dev.siebrenvde.doylcraft.events.*;
import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.handlers.LuckPermsHandler;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.handlers.WorldGuardHandler;
import dev.siebrenvde.doylcraft.tabcompleters.PvPCompleter;
import dev.siebrenvde.doylcraft.tabcompleters.RankCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private LuckPermsHandler lpHandler;
    private DiscordHandler discordHandler;
    private WorldGuardHandler wgHandler;
    private ScoreboardHandler sbHandler;

    @Override
    public void onEnable() {
        instance = this;
        lpHandler = new LuckPermsHandler(this);
        discordHandler = new DiscordHandler();
        wgHandler = new WorldGuardHandler();
        sbHandler = new ScoreboardHandler();
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        getCommand("pvp").setExecutor(new PvP(this));
        getCommand("pvp").setTabCompleter(new PvPCompleter());
        getCommand("rank").setExecutor(new Rank(lpHandler));
        getCommand("rank").setTabCompleter(new RankCompleter(lpHandler));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PetDamageEvent(this), this);
        getServer().getPluginManager().registerEvents(new AFKEvent(this), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(this), this);
        getServer().getPluginManager().registerEvents(new DPlayerJoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new DPlayerDeathEvent(sbHandler), this);
    }

    public LuckPermsHandler getLuckPermsHandler() { return lpHandler; }
    public DiscordHandler getDiscordHandler() { return discordHandler; }
    public WorldGuardHandler getWorlgGuardHandler() { return wgHandler; }
    public ScoreboardHandler getScoreboardHandler() { return sbHandler; }

    public static Main getInstance() { return instance; }

}
