package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static dev.siebrenvde.doylcraft.listeners.ItemDamageListener.COOLDOWN_DURATION_SECONDS;
import static dev.siebrenvde.doylcraft.listeners.ItemDamageListener.LAST_PING_KEY;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
public class DoylCraftCommand extends CommandBase {

    private static final String GITHUB_URL = "https://github.com/Siebrenvde/DoylCraft";

    public static void register(Commands commands) {
        commands.register(
            literal("doylcraft")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(
                        text()
                            .append(text("DoylCraft v"))
                            .append(text(DoylCraft.getInstance().getPluginMeta().getVersion()))
                            .append(text(" by Siebrenvde"))
                            .hoverEvent(HoverEvent.showText(text(GITHUB_URL)))
                            .clickEvent(ClickEvent.openUrl(GITHUB_URL))
                            .build()
                    );
                    return SINGLE_SUCCESS;
                })
                .then(literal("debug")
                    .requires(hasSubPermission("debug"))
                    .then(literal("spawn_wandering_trader")
                        .requires(isPlayer())
                        .executes(withPlayer((ctx, player) -> {
                            // Spawns a Wandering Trader with the NATURAL spawn reason and immediately deletes it
                            player.getWorld().spawnEntity(
                                player.getLocation(),
                                EntityType.WANDERING_TRADER,
                                CreatureSpawnEvent.SpawnReason.NATURAL
                            ).remove();
                        }))
                    )
                    .then(literal("get_durability_cooldown")
                        .requires(isPlayer())
                        .executes(withPlayer((ctx, player) -> {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            PersistentDataContainerView container = item.getPersistentDataContainer();

                            if(!container.has(LAST_PING_KEY, PersistentDataType.LONG)) {
                                player.sendMessage(
                                    text()
                                        .append(Components.itemStack(item))
                                        .append(text(" does not have a cooldown"))
                                );
                                return;
                            }

                            Instant lastPing = Instant.ofEpochMilli(Objects.requireNonNull(container.get(LAST_PING_KEY, PersistentDataType.LONG)));
                            Duration duration = Duration.between(
                                Instant.now(),
                                lastPing.plusSeconds(COOLDOWN_DURATION_SECONDS)
                            );

                            TextComponent.Builder builder = text();
                            builder.append(text("Cooldown for "));
                            builder.append(Components.itemStack(item));

                            if(duration.isNegative()) {
                                builder.append(text(" has expired"));
                            } else {
                                builder.append(text(" will expire in "));
                                builder.append(Components.duration(duration).color(Colours.DATA));
                            }

                            player.sendMessage(builder);
                        }))
                    )
                )
                .then(literal("utils")
                    .then(literal("highlight_entities")
                        .requires(isPlayer().and(hasSubPermission("utils.highlight-entities")))
                        .then(argument("entities", ArgumentTypes.entities())
                            .then(argument("duration", ArgumentTypes.time())
                                .executes(withPlayer((ctx, player) -> {
                                    List<Entity> entities = resolveEntities(ctx);
                                    entities.forEach(entity -> entity.setGlowing(true));

                                    int duration = ctx.getArgument("duration", Integer.class);
                                    ctx.getSource().getSender().sendMessage(
                                        text()
                                            .append(text("Highlighted "))
                                            .append(text(entities.size(), Colours.DATA))
                                            .append(text(" entities for "))
                                            .append(duration >= 20
                                                ? Components.duration(Tick.of(duration)).color(Colours.DATA)
                                                : text(duration + " tick" + (duration != 1 ? "s" : ""), Colours.DATA)
                                            )
                                            .colorIfAbsent(Colours.GENERIC)
                                    );

                                    if(!entities.isEmpty()) {
                                        Bukkit.getScheduler().runTaskLater(
                                            DoylCraft.getInstance(),
                                            () -> entities.forEach(entity -> entity.setGlowing(false)),
                                            ctx.getArgument("duration", Integer.class)
                                        );
                                    }
                                }))
                            )
                        )
                    )
                )
                .build(),
            "The DoylCraft command"
        );
    }

    private static Predicate<CommandSourceStack> hasSubPermission(String permission) {
        return hasPermission("doylcraft.command." + permission);
    }

}
