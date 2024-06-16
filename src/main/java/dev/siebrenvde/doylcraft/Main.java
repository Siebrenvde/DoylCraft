package dev.siebrenvde.doylcraft;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.events.*;
import dev.siebrenvde.doylcraft.handlers.*;
import dev.siebrenvde.doylcraft.tabcompleters.*;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private MemoryHandler memoryHandler;
    private LuckPermsHandler lpHandler;
    private DiscordHandler discordHandler;
    private WorldGuardHandler wgHandler;
    private ScoreboardHandler sbHandler;
    private TimeHandler timeHandler;

    public void onEnable() {
        instance = this;
        memoryHandler = new MemoryHandler();
        lpHandler = new LuckPermsHandler(this);
        discordHandler = new DiscordHandler();
        wgHandler = new WorldGuardHandler();
        sbHandler = new ScoreboardHandler();
        timeHandler = new TimeHandler(memoryHandler);
        DiscordSRV.api.subscribe(new DiscordSRVListener());
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if(voicechatService != null) {
            voicechatService.registerPlugin(new VoicechatHandler());
        }
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        getCommand("pvp").setExecutor(new PvPCommand(this));
        getCommand("pvp").setTabCompleter(new PvPCompleter());
        getCommand("group").setExecutor(new GroupCommand(lpHandler));
        getCommand("group").setTabCompleter(new GroupCompleter(lpHandler));
        getCommand("playtime").setExecutor(new PlayTimeCommand(timeHandler));
        getCommand("getowner").setExecutor(new GetOwnerCommand(memoryHandler));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PetDamageEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new AFKEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new ConnectionEvents(this), this);
        getServer().getPluginManager().registerEvents(new BullseyeEvent(), this);
        getServer().getPluginManager().registerEvents(new TameableInteractEvent(memoryHandler), this);
    }

    public MemoryHandler getMemoryHandler() { return memoryHandler; }
    public LuckPermsHandler getLuckPermsHandler() { return lpHandler; }
    public DiscordHandler getDiscordHandler() { return discordHandler; }
    public WorldGuardHandler getWorldGuardHandler() { return wgHandler; }
    public ScoreboardHandler getScoreboardHandler() { return sbHandler; }
    public TimeHandler getTimeHandler() { return timeHandler; }

    public static Main getInstance() { return instance; }

}