package dev.siebrenvde.doylcraft;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.siebrenvde.doylcraft.addons.*;
import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.listeners.*;
import dev.siebrenvde.doylcraft.handlers.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class DoylCraft extends JavaPlugin {

    @Getter private static DoylCraft instance;
    public static ComponentLogger LOGGER;

    /* Addons */
    private BlueMapAddon blueMapAddon;
    private DiscordSRVAddon discordSRVAddon;
    private LuckPermsAddon luckPermsAddon;
    private WorldGuardAddon worldGuardAddon;

    /* Handlers */
    private ScoreboardHandler scoreboardHandler;

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getComponentLogger();
        initAddons();
        initHandlers();
        registerCommands();
        registerListeners();
    }

    /**
     * Initialise addon classes
     */
    private void initAddons() {
        blueMapAddon = new BlueMapAddon();
        discordSRVAddon = new DiscordSRVAddon();
        luckPermsAddon = new LuckPermsAddon(this);
        worldGuardAddon = new WorldGuardAddon();
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if(voicechatService != null) {
            voicechatService.registerPlugin(new VoicechatAddon());
        }
    }

    /**
     * Initialise handler classes
     */
    private void initHandlers() {
        scoreboardHandler = new ScoreboardHandler();
    }

    /**
     * Register all commands using Brigadier
     */
    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        LifecycleEventManager<Plugin> lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            GetOwnerCommand.register(commands);
            new GroupCommand(luckPermsAddon).register(commands);
            PlayTimeCommand.register(commands);
            new PvPCommand(worldGuardAddon).register(commands);
            DoylCraftCommand.register(commands);
        });
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        registerListeners(
            new PetDamageListener(discordSRVAddon),
            new AFKListener(discordSRVAddon),
            new ChatListener(discordSRVAddon),
            new ConnectionListener(this),
            new BullseyeListener(),
            new PlayerInteractEntityListener(),
            new DismountEntityListener(),
            new MobGriefingListener(),
            new VillagerDeathListener(),
            new WarpModifyListener(blueMapAddon),
            new SilenceEntityListener(),
            new PlayerSleepListener(),
            new WanderingTraderListener()
        );
    }

    /**
     * Register all listeners in an array
     * @param listeners the listeners to register
     */
    private void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

}
