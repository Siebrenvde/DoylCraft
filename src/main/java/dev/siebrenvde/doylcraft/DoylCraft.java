package dev.siebrenvde.doylcraft;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.siebrenvde.doylcraft.addons.*;
import dev.siebrenvde.doylcraft.commands.*;
import dev.siebrenvde.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.doylcraft.listeners.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class DoylCraft extends JavaPlugin {

    @Nullable private static DoylCraft instance;
    @Nullable private static ComponentLogger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getComponentLogger();
        initAddons();
        initHandlers();
        registerCommands();
        registerListeners();
        addServerLinks();
    }

    /**
     * Initialise addon classes
     */
    private void initAddons() {
        new BlueMapAddon();
        new DiscordSRVAddon();
        new LuckPermsAddon();
        new WorldGuardAddon();
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if(voicechatService != null) voicechatService.registerPlugin(new VoicechatAddon());
    }

    /**
     * Initialise handler classes
     */
    private void initHandlers() {
        new ScoreboardHandler();
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
            GroupCommand.register(commands);
            PlayTimeCommand.register(commands);
            PvPCommand.register(commands);
            DoylCraftCommand.register(commands);
            SilenceCommand.register(commands);
            RemainingBiomesCommand.register(commands);
        });
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        registerListeners(
            new PetDamageListener(),
            new AFKListener(),
            new ChatListener(),
            new ConnectionListener(),
            new BullseyeListener(),
            new PlayerInteractEntityListener(),
            new DismountEntityListener(),
            new MobGriefingListener(),
            new VillagerDeathListener(),
            new WarpModifyListener(),
            new SilenceEntityListener(),
            new PlayerSleepListener(),
            new WanderingTraderListener(),
            new ItemDamageListener()
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

    /**
     * Adds our server links
     */
    @SuppressWarnings("UnstableApiUsage")
    private void addServerLinks() {
        try {
            getServer().getServerLinks().addLink(
                Component.text("Discord"),
                new URI(DiscordSRVAddon.get().getInviteLink())
            );
        } catch(URISyntaxException e) {
            logger().error("Failed to add Discord invite link", e);
        }
    }

    public static DoylCraft instance() { return requireNonNull(instance); }
    public static ComponentLogger logger() { return requireNonNull(logger); }

}
