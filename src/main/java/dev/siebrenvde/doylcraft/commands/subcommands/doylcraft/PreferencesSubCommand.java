package dev.siebrenvde.doylcraft.commands.subcommands.doylcraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.player.preferences.menu.DurabilityPingPreferencesMenu;
import dev.siebrenvde.doylcraft.player.preferences.menu.MainPreferencesMenu;
import dev.siebrenvde.doylcraft.player.preferences.menu.PetDamageMessagesPreferencesMenu;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.ZoneId;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

/**
 * Subcommand for {@link dev.siebrenvde.doylcraft.commands.DoylCraftCommand}
 * that allows players to view and edit their preferences
 */
@SuppressWarnings({"UnstableApiUsage", "CodeBlock2Expr"})
@NullMarked
public class PreferencesSubCommand extends CommandBase {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return literal("preferences")
            .requires(isPlayer())
            .executes(withPlayer((ctx, player) -> {
                MainPreferencesMenu.openMenu(player);
            }))
            .then(literal("durability_ping")
                .executes(withPlayer((ctx, player) -> {
                    DurabilityPingPreferencesMenu.openMenu(player);
                }))
                .then(literal("percentage")
                    .then(argument("value", IntegerArgumentType.integer(0, 100))
                        .executes(withPreferences((ctx, player, prefs) -> {
                            int value = IntegerArgumentType.getInteger(ctx, "value");
                            prefs.durabilityPing.percentage.setValue(value);
                            player.sendMessage(
                                text()
                                    .append(text("Set the durability ping percentage to "))
                                    .append(text(value + "%", Colours.DATA))
                            );
                        }))
                    )
                )
                .then(literal("cooldown")
                    .then(argument("value", IntegerArgumentType.integer(0))
                        .executes(withPreferences((ctx, player, prefs) -> {
                            int value = ctx.getArgument("value", Integer.class);
                            prefs.durabilityPing.cooldown.setValue(value);
                            player.sendMessage(
                                text()
                                    .append(text("Set the durability ping cooldown to "))
                                    .append(Components.duration(Duration.ofSeconds(value)).color(Colours.DATA))
                            );
                        }))
                    )
                )
            )
            .then(literal("pet_damage_messages")
                .executes(withPlayer((ctx, player) -> {
                    PetDamageMessagesPreferencesMenu.openMenu(player);
                }))
            )
            .then(literal("timezone")
                .then(argument("timezone", StringArgumentType.greedyString())
                    .suggests((ctx, builder) -> {
                        ZoneId.getAvailableZoneIds().stream()
                            .filter(s -> {
                                if(s.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) return true;
                                for(String part : s.split("/")) {
                                    if(part.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) return true;
                                }
                                return false;
                            })
                            .forEach(builder::suggest);
                        return builder.buildFuture();
                    })
                    .executes(withPreferences((ctx, player, prefs) -> {
                        String timezone = StringArgumentType.getString(ctx, "timezone");

                        if(!ZoneId.getAvailableZoneIds().contains(timezone)) {
                            player.sendMessage(
                                text()
                                    .append(text("Invalid time zone: "))
                                    .append(text(timezone, Colours.DATA))
                                    .color(NamedTextColor.RED)
                            );
                            return;
                        }

                        prefs.timezone.setValue(timezone);
                        player.sendMessage(
                            text()
                                .append(text("Set your time zone to "))
                                .append(text(timezone, Colours.DATA))
                        );
                    }))
                )
            );
    }

    private static Command<CommandSourceStack> withPreferences(PreferenceCommand command) {
        return withPlayer((ctx, player) -> {
            command.run(ctx, player, PlayerData.preferences(player));
        });
    }

    @FunctionalInterface
    private interface PreferenceCommand {
        void run(
            CommandContext<CommandSourceStack> context,
            Player player,
            PlayerPreferences preferences
        ) throws CommandSyntaxException;
    }

}
