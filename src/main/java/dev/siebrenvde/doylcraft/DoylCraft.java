package dev.siebrenvde.doylcraft;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.siebrenvde.doylcraft.addons.*;
import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.events.*;
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
    private MemoryHandler memoryHandler;
    private ScoreboardHandler scoreboardHandler;

    public void onEnable() {
        instance = this;
        LOGGER = getComponentLogger();
        initAddons();
        initHandlers();
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if(voicechatService != null) {
            voicechatService.registerPlugin(new VoicechatAddon());
        }
        registerCommands();
        registerEvents();
    }

    private void initAddons() {
        blueMapAddon = new BlueMapAddon();
        discordSRVAddon = new DiscordSRVAddon();
        luckPermsAddon = new LuckPermsAddon(this);
        worldGuardAddon = new WorldGuardAddon();
    }

    private void initHandlers() {
        memoryHandler = new MemoryHandler();
        scoreboardHandler = new ScoreboardHandler();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        LifecycleEventManager<Plugin> lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            GetOwnerCommand.register(commands, memoryHandler);
            new GroupCommand(luckPermsAddon).register(commands);
            new PlayTimeCommand(memoryHandler).register(commands);
            new PvPCommand(worldGuardAddon).register(commands);
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
            new WarpModifyListener(blueMapAddon)
        );
    }

    private void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

}