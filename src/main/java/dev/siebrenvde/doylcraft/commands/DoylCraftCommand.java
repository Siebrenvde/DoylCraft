package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.DoylCraft;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
public class DoylCraftCommand {

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
                    .requires(source -> source.getSender().hasPermission("doylcraft.command.debug"))
                    .then(literal("spawn_wandering_trader")
                        .requires(source -> source.getSender() instanceof Player)
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();

                            // Spawns a Wandering Trader with the NATURAL spawn reason and immediately deletes it
                            player.getWorld().spawnEntity(
                                player.getLocation(),
                                EntityType.WANDERING_TRADER,
                                CreatureSpawnEvent.SpawnReason.NATURAL
                            ).remove();

                            return SINGLE_SUCCESS;
                        })
                    )
                )
                .build(),
            "The DoylCraft command"
        );
    }

}
