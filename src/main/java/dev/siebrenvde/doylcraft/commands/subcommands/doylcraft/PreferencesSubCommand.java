package dev.siebrenvde.doylcraft.commands.subcommands.doylcraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.siebrenvde.configlib.libs.quilt.config.api.values.TrackedValue;
import dev.siebrenvde.doylcraft.preferences.PlayerPreferences;
import dev.siebrenvde.doylcraft.preferences.Preferences;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

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
public class PreferencesSubCommand extends CommandBase {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return literal("preferences")
            .requires(isPlayer())
            .then(literal("timezone")
                .then(literal("get")
                    .executes(withPreferences((ctx, player, prefs) -> {
                        player.sendMessage(
                            text()
                                .append(text("Your time zone is "))
                                .append(text(prefs.timezone().getId(), Colours.DATA))
                        );
                    }))
                )
                .then(literal("set")
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
                                        .color(Colours.ERROR)
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
                )
            )
            .then(literal("voicechat_reminder")
                .then(literal("get")
                    .executes(withPreferences((ctx, player, prefs) -> {
                        player.sendMessage(
                            text()
                                .append(text("Simple Voice Chat reminder is "))
                                .append(text(
                                    prefs.voicechatReminder()
                                        ? "enabled"
                                        : "disabled",
                                    Colours.DATA
                                ))
                        );
                    }))
                )
                .then(literal("set")
                    .then(argument("value", BoolArgumentType.bool())
                        .executes(withPreferences((ctx, player, prefs) -> {
                            boolean value = BoolArgumentType.getBool(ctx, "value");
                            prefs.voicechatReminder.setValue(value);
                            player.sendMessage(
                                text()
                                    .append(text("Simple Voice Chat reminder is now "))
                                    .append(text(
                                        value
                                            ? "enabled"
                                            : "disabled",
                                        Colours.DATA
                                    ))
                            );
                        }))
                    )
                )
            )
            .then(literal("durability_ping")
                .then(literal("enabled")
                    .then(literal("get")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            player.sendMessage(
                                text()
                                    .append(text("Durability ping is "))
                                    .append(text(
                                        prefs.durabilityPing.enabled()
                                            ? "enabled"
                                            : "disabled",
                                        Colours.DATA
                                    ))
                            );
                        }))
                    )
                    .then(literal("set")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(withPreferences((ctx, player, prefs) -> {
                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                prefs.durabilityPing.enabled.setValue(value);
                                player.sendMessage(
                                    text()
                                        .append(text("Durability ping is now "))
                                        .append(text(
                                            value
                                                ? "enabled"
                                                : "disabled",
                                            Colours.DATA
                                        ))
                                );
                            }))
                        )
                    )
                )
                .then(literal("percentage")
                    .then(literal("get")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            player.sendMessage(
                                text()
                                    .append(text("The durability ping percentage is "))
                                    .append(text(
                                        prefs.durabilityPing.percentage() + "%",
                                        Colours.DATA
                                    ))
                            );
                        }))
                    )
                    .then(literal("set")
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
                    .then(literal("reset")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            resetValue(prefs.durabilityPing.percentage);
                            player.sendMessage(
                                text()
                                    .append(text("Reset the durability ping percentage to "))
                                    .append(text(prefs.durabilityPing.percentage() + "%", Colours.DATA))
                            );
                        }))
                    )
                )
                .then(literal("cooldown")
                    .then(literal("get")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            int value = prefs.durabilityPing.cooldown();
                            player.sendMessage(
                                text()
                                    .append(text("The durability ping cooldown is "))
                                    .append(Components.duration(Duration.ofSeconds(value)).color(Colours.DATA))
                            );
                        }))
                    )
                    .then(literal("set")
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
                    .then(literal("reset")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            resetValue(prefs.durabilityPing.cooldown);
                            player.sendMessage(
                                text()
                                    .append(text("Reset the durability ping cooldown to "))
                                    .append(Components.duration(Duration.ofSeconds(prefs.durabilityPing.cooldown())).color(Colours.DATA))
                            );
                        }))
                    )
                )
            )
            .then(literal("pet_damage_messages")
                .then(literal("broadcast")
                    .then(literal("get")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            player.sendMessage(
                                text()
                                    .append(text("Pet death broadcasts are "))
                                    .append(text(
                                        prefs.petDamageMessages.broadcast()
                                            ? "enabled"
                                            : "disabled",
                                        Colours.DATA
                                    ))
                            );
                        }))
                    )
                    .then(literal("set")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(withPreferences((ctx, player, prefs) -> {
                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                prefs.petDamageMessages.broadcast.setValue(value);
                                player.sendMessage(
                                    text()
                                        .append(text("Pet death broadcasts are now "))
                                        .append(text(
                                            value
                                                ? "enabled"
                                                : "disabled",
                                            Colours.DATA
                                        ))
                                );
                            }))
                        )
                    )
                )
                .then(literal("owner")
                    .then(literal("get")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            player.sendMessage(
                                text()
                                    .append(text("Pet damage owner messages are "))
                                    .append(text(
                                        prefs.petDamageMessages.owner()
                                            ? "enabled"
                                            : "disabled",
                                        Colours.DATA
                                    ))
                            );
                        }))
                    )
                    .then(literal("set")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(withPreferences((ctx, player, prefs) -> {
                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                prefs.petDamageMessages.owner.setValue(value);
                                player.sendMessage(
                                    text()
                                        .append(text("Pet damage owner messages are now "))
                                        .append(text(
                                            value
                                                ? "enabled"
                                                : "disabled",
                                            Colours.DATA
                                        ))
                                );
                            }))
                        )
                    )
                )
                .then(literal("attacker")
                    .then(literal("get")
                        .executes(withPreferences((ctx, player, prefs) -> {
                            player.sendMessage(
                                text()
                                    .append(text("Pet damage attacker messages are "))
                                    .append(text(
                                        prefs.petDamageMessages.attacker()
                                            ? "enabled"
                                            : "disabled",
                                        Colours.DATA
                                    ))
                            );
                        }))
                    )
                    .then(literal("set")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(withPreferences((ctx, player, prefs) -> {
                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                prefs.petDamageMessages.attacker.setValue(value);
                                player.sendMessage(
                                    text()
                                        .append(text("Pet damage attacker messages are now "))
                                        .append(text(
                                            value
                                                ? "enabled"
                                                : "disabled",
                                            Colours.DATA
                                        ))
                                );
                            }))
                        )
                    )
                )
            );
    }

    private static Command<CommandSourceStack> withPreferences(PreferenceCommand command) {
        return withPlayer((ctx, player) -> {
            command.run(ctx, player, Preferences.get(player));
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

    private static <T> void resetValue(TrackedValue<T> value) {
        value.setValue(value.getDefaultValue());
    }

}
