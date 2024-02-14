package dev.siebrenvde.doylcraft;

import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.events.*;
import dev.siebrenvde.doylcraft.handlers.*;
import dev.siebrenvde.doylcraft.tabcompleters.*;
import dev.siebrenvde.doylcraft.handlers.ReloadHandler;
import dev.siebrenvde.doylcraft.utils.Requests;
import github.scarsz.discordsrv.DiscordSRV;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private LuckPermsHandler lpHandler;
    private DiscordHandler discordHandler;
    private WorldGuardHandler wgHandler;
    private ScoreboardHandler sbHandler;
    private TimeHandler timeHandler;
    private Requests requests;
    private ReloadHandler reloadHandler;

    private List<Player> list = new ArrayList();

    public void onEnable() {
        instance = this;
        lpHandler = new LuckPermsHandler(this);
        discordHandler = new DiscordHandler();
        wgHandler = new WorldGuardHandler();
        sbHandler = new ScoreboardHandler();
        timeHandler = new TimeHandler();
        requests = new Requests(this);
        reloadHandler = new ReloadHandler(this);
        DiscordSRV.api.subscribe(new DiscordSRVListener());
        registerCommands();
        registerEvents();
        if(System.getProperty("ENABLED") == null) {
            System.setProperty("ENABLED", "TRUE");
        } else {
            getLogger().warning("Reloaded detected. Loading reload data.");
            reloadHandler.loadData();
        }
    }

    public void onDisable() {
        reloadHandler.saveData();
    }

    private void registerCommands() {
        getCommand("pvp").setExecutor(new PvP(this));
        getCommand("pvp").setTabCompleter(new PvPCompleter());
        getCommand("rank").setExecutor(new Rank(lpHandler));
        getCommand("rank").setTabCompleter(new RankCompleter(lpHandler));
        getCommand("playtime").setExecutor(new PlayTime(timeHandler));
        getCommand("streams").setExecutor(new Streams(this));
        getCommand("getowner").setExecutor(new GetOwner(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PetDamageEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new AFKEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new ConnectionEvents(this), this);
        getServer().getPluginManager().registerEvents(new DPlayerDeathEvent(sbHandler), this);
        getServer().getPluginManager().registerEvents(new BullseyeEvent(), this);
        getServer().getPluginManager().registerEvents(new TameableInteractEvent(this), this);
    }

    public LuckPermsHandler getLuckPermsHandler() { return lpHandler; }
    public DiscordHandler getDiscordHandler() { return discordHandler; }
    public WorldGuardHandler getWorldGuardHandler() { return wgHandler; }
    public ScoreboardHandler getScoreboardHandler() { return sbHandler; }
    public TimeHandler getTimeHandler() { return timeHandler; }
    public Requests getRequests() { return requests; }

    public static Main getInstance() { return instance; }

    public boolean listContains(Player player) {
        return list.contains(player);
    }

    public void addListPlayer(Player player) {
        list.add(player);
    }

    public void removeListPlayer(Player player) {
        list.remove(player);
    }

}