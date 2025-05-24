package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.siebrenvde.doylcraft.addons.EssentialsAddon;
import dev.siebrenvde.doylcraft.player.home.Home;
import dev.siebrenvde.doylcraft.player.home.HomeMenu;
import dev.siebrenvde.doylcraft.player.home.Homes;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.siebrenvde.doylcraft.commands.arguments.HomeArgumentType.getHome;
import static dev.siebrenvde.doylcraft.commands.arguments.HomeArgumentType.home;
import static dev.siebrenvde.doylcraft.location.NamedLocation.DISPLAY_NAME_MM;
import static dev.siebrenvde.doylcraft.player.PlayerData.homes;
import static dev.siebrenvde.doylcraft.player.PlayerData.preferences;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.resource;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.callback;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class HomeCommands extends CommandBase {

    public static void register(Commands commands) {

        commands.register(literal("home")
            .requires(isPlayer())
            .executes(ctx -> {
                Player player = (Player) ctx.getSource().getSender();
                Homes homes = homes(player);
                if (homes.size() == 1) {
                    teleportToHome(player, homes.iterator().next());
                    return 1;
                } else if (homes.has("home")) {
                    teleportToHome(player, homes.getOrThrow("home"));
                    return 1;
                }
                return openOrListHomes(ctx);
            })
            .then(argument("home", home())
                .executes(withHome((ctx, player, home) -> {
                    teleportToHome(player, home);
                    return 1;
                }))
            )
            .build()
        );

        commands.register(literal("sethome")
            .requires(isPlayer())
            .executes(ctx -> executeSetHome(ctx, "home", text("Home")))
            .then(argument("key", word())
                .executes(ctx -> {
                    String key = getString(ctx, "key");
                    return executeSetHome(ctx, key, text(key));
                })
                .then(argument("display_name", greedyString())
                    .executes(ctx -> executeSetHome(
                        ctx,
                        getString(ctx, "key"),
                        DISPLAY_NAME_MM.deserialize(getString(ctx, "display_name"))
                    ))
                )
            )
            .build()
        );

        commands.register(literal("delhome")
            .requires(isPlayer())
            .then(argument("home", home())
                .executes(withHome((ctx, player, home) -> {
                    homes(player).remove(home);
                    player.sendMessage(
                        text()
                            .append(text("Deleted home ["))
                            .append(home.displayName())
                            .append(text("]"))
                            .color(Colours.GENERIC)
                    );
                    homes(player).save();
                    return 1;
                }))
            )
            .build()
        );

        commands.register(literal("homes")
            .requires(isPlayer())
            .executes(HomeCommands::openOrListHomes)
            .then(literal("list")
                .executes(HomeCommands::listHomes)
            )
            .then(literal("edit")
                .then(argument("home", home())
                    .then(literal("display_name")
                        .then(argument("display_name", greedyString())
                            .executes(withHome((ctx, player, home) -> {
                                home.displayName(DISPLAY_NAME_MM.deserialize(getString(ctx, "display_name")));
                                homes(player).save();
                                player.sendMessage(
                                    text()
                                        .append(text("Changed display name to ["))
                                        .append(home.displayName())
                                        .append(text("]"))
                                        .color(Colours.GENERIC)
                                );
                                return 1;
                            }))
                        )
                    )
                    .then(literal("icon")
                        .then(argument("icon", resource(RegistryKey.ITEM))
                            .executes(withHome((ctx, player, home) -> {
                                ItemType icon = ctx.getArgument("icon", ItemType.class);
                                home.icon(icon);
                                homes(player).save();
                                player.sendMessage(text("Changed icon to ", Colours.GENERIC).append(translatable(icon, Colours.DATA)));
                                return 1;
                            }))
                        )
                    )
                )
            )
            .build()
        );

    }

    private static int openOrListHomes(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        if (preferences(player).useTeleportMenus()) {
            HomeMenu.tryOpen(player);
            return 1;
        }
        return listHomes(ctx);
    }

    private static int listHomes(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        player.sendMessage(
            text()
                .append(text("- ", NamedTextColor.DARK_AQUA))
                .append(text("Homes", NamedTextColor.AQUA))
                .append(text(" -", NamedTextColor.DARK_AQUA))
        );
        homes(player).forEach(player::sendMessage);
        return homes(player).size();
    }

    private static void teleportToHome(Player player, Home home) {
        EssentialsAddon.teleport(player, home.location(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(result -> {
            player.sendMessage(
                text()
                    .append(text("Teleporting to "))
                    .append(text("["))
                    .append(home)
                    .append(text("]"))
                    .color(Colours.GENERIC)
            );
        });
    }

    private static int executeSetHome(CommandContext<CommandSourceStack> ctx, String key, Component displayName) {
        Player player = (Player) ctx.getSource().getSender();
        return createHome(
            player,
            new Home(key, player.getLocation(), displayName),
            preferences(player).replaceHomes()
        );
    }

    private static int createHome(Player player, Home home, boolean force) {
        Homes homes = homes(player);
        if (force) {
            homes.add(home);
        } else {
            Home existing = homes.addIfAbsent(home);
            if (existing != null) {
                player.sendMessage(
                    text()
                        .append(text("A home with key '"))
                        .append(text(home.key()))
                        .append(text("' ("))
                        .append(existing)
                        .append(text(") already exists"))
                        .appendNewline()
                        .append(text("Click "))
                        .append(
                            text("here")
                                .decorate(TextDecoration.UNDERLINED)
                                .clickEvent(callback(audience -> createHome(player, home, true)))
                        )
                        .append(text(" to replace it"))
                        .color(NamedTextColor.RED)
                );
                return 0;
            }
        }

        player.sendMessage(
            text()
                .append(text("Set home ["))
                .append(home)
                .append(text("] to current location"))
                .color(Colours.GENERIC)
        );

        homes.save();
        return 1;
    }

    private static Command<CommandSourceStack> withHome(HomeCommand command) {
        return ctx -> command.run(
            ctx,
            (Player) ctx.getSource().getSender(),
            getHome(ctx, "home")
        );
    }

    @FunctionalInterface
    private interface HomeCommand {
        int run(CommandContext<CommandSourceStack> context, Player player, Home home) throws CommandSyntaxException;
    }

}
