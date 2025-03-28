package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.siebrenvde.doylcraft.addons.BlueMapAddon;
import dev.siebrenvde.doylcraft.addons.EssentialsAddon;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import dev.siebrenvde.doylcraft.warp.Warp;
import dev.siebrenvde.doylcraft.warp.Warps;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Rotation;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.siebrenvde.doylcraft.commands.arguments.WarpArgumentType.getWarp;
import static dev.siebrenvde.doylcraft.commands.arguments.WarpArgumentType.warp;
import static dev.siebrenvde.doylcraft.warp.Warps.WARPS;
import static dev.siebrenvde.doylcraft.warp.Warps.saveWarps;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.finePosition;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.rotation;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.world;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class WarpCommands extends CommandBase {

    private static final MiniMessage displayNameMM = MiniMessage.builder()
        .tags(TagResolver.resolver(
            StandardTags.decorations(),
            StandardTags.color(),
            StandardTags.keybind(),
            StandardTags.translatable(),
            StandardTags.translatableFallback(),
            StandardTags.font(),
            StandardTags.gradient(),
            StandardTags.rainbow(),
            StandardTags.reset(),
            StandardTags.pride(),
            StandardTags.shadowColor()
        ))
        .build();

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
            .requires(isPlayer().and(hasPermission("doylcraft.command.setwarp")))
            .then(argument("key", word())
                .then(argument("display_name", greedyString())
                    .executes(ctx -> {
                        Player player = (Player) ctx.getSource().getSender();

                        Warp warp = new Warp(
                            getString(ctx, "key"),
                            displayNameMM.deserialize(getString(ctx, "display_name")),
                            player.getLocation()
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

                        BlueMapAddon.get().addMarker(warp);
                        saveWarps();
                        return 1;
                    })
                )
            )
            .build()
        );

        commands.register(literal("delwarp")
            .requires(hasPermission("doylcraft.command.delwarp"))
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
                        BlueMapAddon.get().removeMarker(warp);
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
                .requires(hasPermission("doylcraft.command.warps.reload"))
                .executes(ctx -> {
                    WARPS.clear();
                    Warps.loadWarps(false);
                    BlueMapAddon.get().populateWarps();
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
                .requires(hasPermission("doylcraft.command.warps.edit"))
                .then(argument("warp", warp())
                    .then(literal("display_name")
                        .then(argument("display_name", greedyString())
                            .executes(withWarp((ctx, sender, warp) -> {
                                Warp oldWarp = warp.copy();
                                warp.displayName(displayNameMM.deserialize(getString(ctx, "display_name")));
                                saveWarps();
                                BlueMapAddon.get().updateMarker(oldWarp, warp);
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
                    .then(literal("location")
                        .then(literal("here")
                            .requires(isPlayer())
                            .executes(ctx -> setLocation(ctx, ((Player) ctx.getSource().getSender()).getLocation()))
                        )
                        .then(argument("position", finePosition())
                            .then(argument("rotation", rotation())
                                .then(argument("world", world())
                                    .executes(ctx -> {
                                        FinePosition pos = ctx.getArgument("position", FinePositionResolver.class).resolve(ctx.getSource());
                                        Rotation rot = ctx.getArgument("rotation", RotationResolver.class).resolve(ctx.getSource());
                                        World world = ctx.getArgument("world", World.class);
                                        return setLocation(ctx, pos.toLocation(world).setRotation(rot));
                                    })
                                )
                            )
                        )
                    )
                )
            )
            .build()
        );

    }

    private static int openOrListWarps(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getSender() instanceof Player player) {
            // TODO: Open GUI
            return listWarps(ctx);
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

    private static int setLocation(CommandContext<CommandSourceStack> ctx, Location location) {
        Warp warp = getWarp(ctx, "warp");
        Warp oldWarp = warp.copy();
        warp.location(location);
        BlueMapAddon.get().updateMarker(oldWarp, warp);
        saveWarps();
        ctx.getSource().getSender().sendMessage(
            text("Changed location to ").append(Components.location(location))
        );
        return 1;
    }

    private static Command<CommandSourceStack> withWarp(WarpCommand command) {
        return ctx -> command.run(ctx, ctx.getSource().getSender(), getWarp(ctx, "warp"));
    }

    @FunctionalInterface
    private interface WarpCommand {
        int run(CommandContext<CommandSourceStack> context, CommandSender sender, Warp warp) throws CommandSyntaxException;
    }

}
