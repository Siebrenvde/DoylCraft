package dev.siebrenvde.doylcraft;

import dev.siebrenvde.doylcraft.commands.PlayTime;
import dev.siebrenvde.doylcraft.commands.PvP;
import dev.siebrenvde.doylcraft.commands.Rank;
import dev.siebrenvde.doylcraft.events.*;
import dev.siebrenvde.doylcraft.handlers.*;
import dev.siebrenvde.doylcraft.tabcompleters.PvPCompleter;
import dev.siebrenvde.doylcraft.tabcompleters.RankCompleter;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private LuckPermsHandler lpHandler;
    private DiscordHandler discordHandler;
    private WorldGuardHandler wgHandler;
    private ScoreboardHandler sbHandler;
    private TimeHandler timeHandler;

    @Override
    public void onEnable() {
        instance = this;
        lpHandler = new LuckPermsHandler(this);
        discordHandler = new DiscordHandler();
        wgHandler = new WorldGuardHandler();
        sbHandler = new ScoreboardHandler();
        timeHandler = new TimeHandler();
        DiscordSRV.api.subscribe(new DiscordSRVListener());
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        getCommand("pvp").setExecutor(new PvP(this));
        getCommand("pvp").setTabCompleter(new PvPCompleter());
        getCommand("rank").setExecutor(new Rank(lpHandler));
        getCommand("rank").setTabCompleter(new RankCompleter(lpHandler));
        getCommand("playtime").setExecutor(new PlayTime(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PetDamageEvent(this), this);
        getServer().getPluginManager().registerEvents(new AFKEvent(this), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(this), this);
        getServer().getPluginManager().registerEvents(new ConnectionEvents(this), this);
        getServer().getPluginManager().registerEvents(new DPlayerDeathEvent(sbHandler), this);
        getServer().getPluginManager().registerEvents(new BullseyeEvent(), this);
    }

    public LuckPermsHandler getLuckPermsHandler() { return lpHandler; }
    public DiscordHandler getDiscordHandler() { return discordHandler; }
    public WorldGuardHandler getWorldGuardHandler() { return wgHandler; }
    public ScoreboardHandler getScoreboardHandler() { return sbHandler; }
    public TimeHandler getTimeHandler() { return timeHandler; }

    public static Main getInstance() { return instance; }

}
