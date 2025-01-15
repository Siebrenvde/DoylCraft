package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.siebrenvde.doylcraft.addons.WorldGuardAddon;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;
import static org.bukkit.Bukkit.getWorlds;

/**
 * Command to toggle PvP on or off globally or for a specific world
 */
@SuppressWarnings("UnstableApiUsage")
public class PvPCommand {

    private final WorldGuardAddon worldGuardAddon;

    public PvPCommand(WorldGuardAddon worldGuardAddon) {
        this.worldGuardAddon = worldGuardAddon;
    }

    public void register(Commands commands) {
        commands.register(
            Commands.literal("pvp")
                .requires(source -> hasPermission(source, "query"))
                .executes(this::queryAllWorldStates)
                .then(
                    Commands.literal("on")
                        .requires(source -> hasPermission(source, "update"))
                        .executes(ctx -> updateAllWorldStates(ctx, true))
                        .then(Commands.argument("world", ArgumentTypes.world())
                            .executes(ctx -> updateWorldState(ctx, true))
                        )
                )
                .then(
                    Commands.literal("off")
                        .requires(source -> hasPermission(source, "update"))
                        .executes(ctx -> updateAllWorldStates(ctx, false))
                        .then(Commands.argument("world", ArgumentTypes.world())
                            .executes(ctx -> updateWorldState(ctx, false))
                        )
                )
            .build(),
            "Toggle PvP on or off"
        );
    }

    private static boolean hasPermission(CommandSourceStack source, String permission) {
        return source.getSender().hasPermission("doylcraft.command.pvp." + permission);
    }

    private int queryAllWorldStates(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        List<World> enabled = new ArrayList<>();
        List<World> disabled = new ArrayList<>();

        for(World world : getWorlds()) {
            if(getState(world)) {
                enabled.add(world);
            } else {
                disabled.add(world);
            }
        }

        if(enabled.isEmpty() || disabled.isEmpty()) {
            sender.sendMessage(text("PvP is ", Colours.GENERIC)
                .append(disabled.isEmpty() ? text("enabled", Colours.POSITIVE) : text("disabled", Colours.NEGATIVE))
                .append(text(" in "))
                .append(
                    text("all worlds")
                        .hoverEvent(HoverEvent.hoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            text(getWorlds().stream().map(w -> w.getKey().toString()).collect(Collectors.joining("\n")))
                        ))
                )
            );
            return Command.SINGLE_SUCCESS;
        }

        sender.sendMessage(
            worldListStateComponent(enabled, true)
                .append(newline())
                .append(worldListStateComponent(disabled, false))
        );

        return Command.SINGLE_SUCCESS;
    }

    private int updateAllWorldStates(CommandContext<CommandSourceStack> context, boolean state) {
        CommandSender sender = context.getSource().getSender();

        setAllStates(state);
        sender.sendMessage(
            empty().color(Colours.GENERIC)
                .append(state ? Component.text("Enabled", Colours.POSITIVE) : Component.text("Disabled", Colours.NEGATIVE))
                .append(Component.text(" PvP in ")
                .append(
                    text("all worlds")
                        .hoverEvent(HoverEvent.hoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            text(getWorlds().stream().map(w -> w.getKey().toString()).collect(Collectors.joining("\n")))
                        ))
                ))
        );

        return Command.SINGLE_SUCCESS;
    }

    private int updateWorldState(CommandContext<CommandSourceStack> context, boolean state) {
        CommandSender sender = context.getSource().getSender();
        World world = context.getArgument("world", World.class);

        setState(world, state);
        sender.sendMessage(
            empty().color(Colours.GENERIC)
                .append(state ? Component.text("Enabled", Colours.POSITIVE) : Component.text("Disabled", Colours.NEGATIVE))
                .append(Component.text(" PvP in "))
                .append(Components.worldName(world).color(Colours.DATA))
        );

        return Command.SINGLE_SUCCESS;
    }

    private boolean getState(World world) {
        ProtectedRegion region = worldGuardAddon.getOrCreateGlobalRegion(world);
        StateFlag.State state = region.getFlag(Flags.PVP);
        return state == StateFlag.State.ALLOW || state == null;
    }

    private void setState(World world, boolean state) {
        ProtectedRegion region = worldGuardAddon.getOrCreateGlobalRegion(world);
        region.setFlag(Flags.PVP, state ? StateFlag.State.ALLOW : StateFlag.State.DENY);
    }

    private void setAllStates(boolean state) {
        for(World world : getWorlds()) {
            setState(world, state);
        }
    }

    private TextComponent worldListStateComponent(List<World> worlds, boolean state) {
        return text("PvP is ", Colours.GENERIC)
            .append(state ? text("enabled", Colours.POSITIVE) : text("disabled", Colours.NEGATIVE))
            .append(text(" in ", Colours.GENERIC))
            .append(
                join(
                    JoinConfiguration.separator(Component.text(", ", Colours.GENERIC)),
                    worlds.stream().map(world -> Components.worldName(world).color(Colours.DATA)).collect(Collectors.toList())
                )
            );
    }

}
