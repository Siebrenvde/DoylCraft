package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.siebrenvde.doylcraft.addons.WorldGuardAddon;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import dev.siebrenvde.doylcraft.utils.Permissions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;
import static org.bukkit.Bukkit.getWorlds;

/**
 * Command to toggle PvP on or off globally or for a specific world
 */
@NullMarked
public class PvPCommand extends CommandBase {

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("pvp")
                .requires(Permissions.COMMAND_PVP_QUERY)
                .executes(queryAllWorldStates())
                .then(Commands.literal("on")
                    .requires(Permissions.COMMAND_PVP_UPDATE)
                    .executes(updateAllWorldStates(true))
                    .then(Commands.argument("world", ArgumentTypes.world())
                        .executes(updateWorldState(true))
                    )
                )
                .then(Commands.literal("off")
                    .requires(Permissions.COMMAND_PVP_UPDATE)
                    .executes(updateAllWorldStates(false))
                    .then(Commands.argument("world", ArgumentTypes.world())
                        .executes(updateWorldState(false))
                    )
                )
                .build(),
            "Toggle PvP on or off"
        );
    }

    private static Command<CommandSourceStack> queryAllWorldStates() {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();

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
                sender.sendMessage(
                    text()
                        .append(text("PvP is "))
                        .append(disabled.isEmpty() ? text("enabled", NamedTextColor.GREEN) : text("disabled", NamedTextColor.RED))
                        .append(text(" in "))
                        .append(allWorlds())
                        .color(Colours.GENERIC)
                );
                return Command.SINGLE_SUCCESS;
            }

            sender.sendMessage(
                text()
                    .append(worldListStateComponent(enabled, true))
                    .appendNewline()
                    .append(worldListStateComponent(disabled, false))
            );

            return Command.SINGLE_SUCCESS;
        };
    }

    private static Command<CommandSourceStack> updateAllWorldStates(boolean state) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();

            setAllStates(state);
            sender.sendMessage(
                text()
                    .color(Colours.GENERIC)
                    .append(state ? Component.text("Enabled", NamedTextColor.GREEN) : Component.text("Disabled", NamedTextColor.RED))
                    .append(Component.text(" PvP in "))
                    .append(allWorlds())
            );

            return Command.SINGLE_SUCCESS;
        };
    }

    private static Component allWorlds() {
        return text("all worlds")
            .hoverEvent(HoverEvent.hoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                join(
                    JoinConfiguration.newlines(),
                    getWorlds().stream().map(w -> text(w.getKey().toString())).toList()
                )
            ));
    }

    private static Command<CommandSourceStack> updateWorldState(boolean state) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            World world = ctx.getArgument("world", World.class);

            setState(world, state);
            sender.sendMessage(
                text()
                    .color(Colours.GENERIC)
                    .append(state ? Component.text("Enabled", NamedTextColor.GREEN) : Component.text("Disabled", NamedTextColor.RED))
                    .append(Component.text(" PvP in "))
                    .append(Components.worldName(world).color(Colours.DATA))
            );

            return Command.SINGLE_SUCCESS;
        };
    }

    private static boolean getState(World world) {
        ProtectedRegion region = WorldGuardAddon.get().getOrCreateGlobalRegion(world);
        StateFlag.State state = region.getFlag(Flags.PVP);
        return state == StateFlag.State.ALLOW || state == null;
    }

    private static void setState(World world, boolean state) {
        ProtectedRegion region = WorldGuardAddon.get().getOrCreateGlobalRegion(world);
        region.setFlag(Flags.PVP, state ? StateFlag.State.ALLOW : StateFlag.State.DENY);
    }

    private static void setAllStates(boolean state) {
        for(World world : getWorlds()) {
            setState(world, state);
        }
    }

    private static TextComponent worldListStateComponent(List<World> worlds, boolean state) {
        return
            text()
                .append(text("PvP is ", Colours.GENERIC))
                .append(state ? text("enabled", NamedTextColor.GREEN) : text("disabled", NamedTextColor.RED))
                .append(text(" in ", Colours.GENERIC))
                .append(
                    join(
                        JoinConfiguration.separator(Component.text(", ", Colours.GENERIC)),
                        worlds.stream().map(world -> Components.worldName(world).color(Colours.DATA)).collect(Collectors.toList())
                    )
                )
                .build();
    }

}
