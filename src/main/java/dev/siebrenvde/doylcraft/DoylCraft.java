package dev.siebrenvde.doylcraft;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.events.*;
import dev.siebrenvde.doylcraft.handlers.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DoylCraft extends JavaPlugin {

    private static DoylCraft instance;
    public static ComponentLogger LOGGER;

    /* Addons */
    private DiscordSRVAddon discordSRVAddon;

    /* Handlers */
    private MemoryHandler memoryHandler;
    private LuckPermsHandler lpHandler;
    private WorldGuardHandler wgHandler;
    private ScoreboardHandler sbHandler;
    private BlueMapHandler blueMapHandler;

    public void onEnable() {
        instance = this;
        LOGGER = getComponentLogger();
        initAddons();
        initHandlers();
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if(voicechatService != null) {
            voicechatService.registerPlugin(new VoicechatHandler());
        }
        registerCommands();
        registerEvents();
    }

    private void initAddons() {
        discordSRVAddon = new DiscordSRVAddon();
    }

    private void initHandlers() {
        memoryHandler = new MemoryHandler();
        lpHandler = new LuckPermsHandler(this);
        wgHandler = new WorldGuardHandler();
        sbHandler = new ScoreboardHandler();
        blueMapHandler = new BlueMapHandler();
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
        registerListeners(
            new PetDamageEvent(discordSRVAddon),
            new AFKEvent(discordSRVAddon),
            new ChatEvent(discordSRVAddon),
            new ConnectionEvents(this),
            new BullseyeEvent(),
            new TameableInteractEvent(memoryHandler),
            new DismountEntityEvent(),
            new MobGriefingEvents(),
            new VillagerDeathEvent(),
            new WarpModifyListener(blueMapHandler)
        );
    }

    private void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public MemoryHandler getMemoryHandler() { return memoryHandler; }
    public LuckPermsHandler getLuckPermsHandler() { return lpHandler; }
    public DiscordSRVAddon getDiscordSRVAddon() { return discordSRVAddon; }
    public WorldGuardHandler getWorldGuardHandler() { return wgHandler; }
    public ScoreboardHandler getScoreboardHandler() { return sbHandler; }

    public static DoylCraft getInstance() { return instance; }

}