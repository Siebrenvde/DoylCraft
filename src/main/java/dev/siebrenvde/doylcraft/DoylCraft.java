package dev.siebrenvde.doylcraft;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.events.*;
import dev.siebrenvde.doylcraft.handlers.*;
import github.scarsz.discordsrv.DiscordSRV;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class DoylCraft extends JavaPlugin {

    private static DoylCraft instance;
    public static Logger LOGGER;

    private MemoryHandler memoryHandler;
    private LuckPermsHandler lpHandler;
    private DiscordHandler discordHandler;
    private WorldGuardHandler wgHandler;
    private ScoreboardHandler sbHandler;

    public void onEnable() {
        instance = this;
        LOGGER = getLogger();
        initHandlers();
        DiscordSRV.api.subscribe(new DiscordSRVListener());
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if(voicechatService != null) {
            voicechatService.registerPlugin(new VoicechatHandler());
        }
        registerCommands();
        registerEvents();
    }

    private void initHandlers() {
        memoryHandler = new MemoryHandler();
        lpHandler = new LuckPermsHandler(this);
        discordHandler = new DiscordHandler();
        wgHandler = new WorldGuardHandler();
        sbHandler = new ScoreboardHandler();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        LifecycleEventManager<Plugin> lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            GetOwnerCommand.register(commands, memoryHandler);
            new GroupCommand(lpHandler).register(commands);
            new PlayTimeCommand(memoryHandler).register(commands);
            new PvPCommand(wgHandler).register(commands);
        });
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PetDamageEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new AFKEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(discordHandler), this);
        getServer().getPluginManager().registerEvents(new ConnectionEvents(this), this);
        getServer().getPluginManager().registerEvents(new BullseyeEvent(), this);
        getServer().getPluginManager().registerEvents(new TameableInteractEvent(memoryHandler), this);
        getServer().getPluginManager().registerEvents(new DismountEntityEvent(), this);
        getServer().getPluginManager().registerEvents(new MobGriefingEvents(), this);
        getServer().getPluginManager().registerEvents(new VillagerDeathEvent(), this);
    }

    public MemoryHandler getMemoryHandler() { return memoryHandler; }
    public LuckPermsHandler getLuckPermsHandler() { return lpHandler; }
    public DiscordHandler getDiscordHandler() { return discordHandler; }
    public WorldGuardHandler getWorldGuardHandler() { return wgHandler; }
    public ScoreboardHandler getScoreboardHandler() { return sbHandler; }

    public static DoylCraft getInstance() { return instance; }

}