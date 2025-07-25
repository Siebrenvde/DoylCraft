package dev.siebrenvde.doylcraft.commands.subcommands.doylcraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import dev.siebrenvde.doylcraft.utils.Permissions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static dev.siebrenvde.doylcraft.listeners.ItemDamageListener.LAST_PING_KEY;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

/**
 * Subcommand for {@link dev.siebrenvde.doylcraft.commands.DoylCraftCommand}
 * that provides some debug commands
 */
@NullMarked
public class DebugSubCommand extends CommandBase {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return literal("debug")
            .requires(Permissions.COMMAND_DEBUG)
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
                        lastPing.plusSeconds(PlayerData.preferences(player).durabilityPing.cooldown())
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
            );
    }

}
