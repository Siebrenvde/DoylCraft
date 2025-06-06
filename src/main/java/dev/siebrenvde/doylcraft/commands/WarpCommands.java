package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.siebrenvde.doylcraft.addons.BlueMapAddon;
import dev.siebrenvde.doylcraft.addons.EssentialsAddon;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Permissions;
import dev.siebrenvde.doylcraft.warp.Warp;
import dev.siebrenvde.doylcraft.warp.WarpMenu;
import dev.siebrenvde.doylcraft.warp.Warps;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.siebrenvde.doylcraft.commands.arguments.WarpArgumentType.getWarp;
import static dev.siebrenvde.doylcraft.commands.arguments.WarpArgumentType.warp;
import static dev.siebrenvde.doylcraft.location.NamedLocation.DISPLAY_NAME_MM;
import static dev.siebrenvde.doylcraft.player.PlayerData.preferences;
import static dev.siebrenvde.doylcraft.warp.Warps.WARPS;
import static dev.siebrenvde.doylcraft.warp.Warps.saveWarps;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.resource;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NullMarked
public class WarpCommands extends CommandBase {

    public static void register(Commands commands) {

        commands.register(literal("warp")
            .requires(isPlayer())
            .executes(WarpCommands::openOrListWarps)
            .then(argument("warp", warp())
                .executes(withWarp((ctx, sender, warp) -> {
                    EssentialsAddon.teleport((Player) sender, warp.location(), TeleportCause.COMMAND).thenAcceptAsync(result -> {
                        sender.sendMessage(
                            text()
                                .append(text("Warping to "))
                                .append(text("["))
                                .append(warp)
                                .append(text("]"))
                                .color(Colours.GENERIC)
                        );
                    });
                    return 1;
                }))
            )
            .build()
        );

        commands.register(literal("setwarp")
            .requires(isPlayer().and(Permissions.COMMAND_SETWARP))
            .then(argument("key", word())
                .then(argument("display_name", greedyString())
                    .executes(ctx -> {
                        Player player = (Player) ctx.getSource().getSender();

                        Warp warp = new Warp(
                            getString(ctx, "key"),
                            player.getLocation(),
                            DISPLAY_NAME_MM.deserialize(getString(ctx, "display_name"))
                        );

                        Warp existing = Warps.WARPS.putIfAbsent(warp.key(), warp);
                        if (existing != null) {
                            player.sendMessage(
                                text()
                                    .append(text("A warp with key '"))
                                    .append(text(warp.key()))
                                    .append(text("' ("))
                                    .append(existing)
                                    .append(text(") already exists"))
                                    .color(NamedTextColor.RED)
                            );
                            return 0;
                        }

                        player.sendMessage(
                            text()
                                .append(text("Created new warp ["))
                                .append(warp)
                                .append(text("]"))
                                .color(Colours.GENERIC)
                        );

                        BlueMapAddon.addMarker(warp);
                        saveWarps();
                        return 1;
                    })
                )
            )
            .build()
        );

        commands.register(literal("delwarp")
            .requires(Permissions.COMMAND_DELWARP)
                .then(argument("warp", warp())
                    .executes(withWarp((ctx, sender, warp) -> {
                        WARPS.remove(warp.key());
                        sender.sendMessage(
                            text()
                                .append(text("Deleted warp ["))
                                .append(warp.displayName())
                                .append(text("]"))
                                .color(Colours.GENERIC)
                        );
                        BlueMapAddon.removeMarker(warp);
                        saveWarps();
                        return 1;
                    }))
                )
            .build()
        );

        commands.register(literal("warps")
            .executes(WarpCommands::openOrListWarps)
            .then(literal("list")
                .executes(WarpCommands::listWarps)
            )
            .then(literal("reload")
                .requires(Permissions.COMMAND_WARPS_RELOAD)
                .executes(ctx -> {
                    WARPS.clear();
                    Warps.loadWarps(false);
                    BlueMapAddon.populateWarps();
                    int amount = WARPS.size();
                    ctx.getSource().getSender().sendMessage(
                        text()
                            .append(text("Loaded "))
                            .append(text(amount, Colours.DATA))
                            .append(text(" warp" + (amount != 1 ? "s" : "")))
                            .color(Colours.GENERIC)
                    );
                    return amount;
                })
            )
            .then(literal("edit")
                .requires(Permissions.COMMAND_WARPS_EDIT)
                .then(argument("warp", warp())
                    .then(literal("display_name")
                        .then(argument("display_name", greedyString())
                            .executes(withWarp((ctx, sender, warp) -> {
                                Warp oldWarp = warp.copy();
                                warp.displayName(DISPLAY_NAME_MM.deserialize(getString(ctx, "display_name")));
                                saveWarps();
                                BlueMapAddon.updateMarker(oldWarp, warp);
                                ctx.getSource().getSender().sendMessage(
                                    text()
                                        .append(text("Changed display name to ["))
                                        .append(warp.displayName())
                                        .append(text("]"))
                                );
                                return 1;
                            }))
                        )
                    )
                    .then(literal("icon")
                        .then(argument("icon", resource(RegistryKey.ITEM))
                            .executes(withWarp((ctx, sender, warp) -> {
                                ItemType icon = ctx.getArgument("icon", ItemType.class);
                                warp.icon(icon);
                                saveWarps();
                                sender.sendMessage(text("Changed icon to ").append(translatable(icon, Colours.DATA)));
                                return 1;
                            }))
                        )
                    )
                )
            )
            .build()
        );

    }

    private static int openOrListWarps(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getSender() instanceof Player player && preferences(player).useTeleportMenus()) {
            WarpMenu.tryOpen(player);
            return 1;
        }
        return listWarps(ctx);
    }

    private static int listWarps(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        sender.sendMessage(
            text()
                .append(text("- ", NamedTextColor.DARK_AQUA))
                .append(text("Warps", NamedTextColor.AQUA))
                .append(text(" -", NamedTextColor.DARK_AQUA))
        );
        WARPS.values().forEach(sender::sendMessage);
        return WARPS.size();
    }

    private static Command<CommandSourceStack> withWarp(WarpCommand command) {
        return ctx -> command.run(ctx, ctx.getSource().getSender(), getWarp(ctx, "warp"));
    }

    @FunctionalInterface
    private interface WarpCommand {
        int run(CommandContext<CommandSourceStack> context, CommandSender sender, Warp warp) throws CommandSyntaxException;
    }

}
