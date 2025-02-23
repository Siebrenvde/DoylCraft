package dev.siebrenvde.doylcraft.commands.subcommands.doylcraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Predicate;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

/**
 * Subcommand for {@link dev.siebrenvde.doylcraft.commands.DoylCraftCommand}
 * that provides some utility commands
 */
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class UtilsSubCommand extends CommandBase {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return literal("utils")
            .then(literal("highlight_entities")
                .requires(isPlayer().and(hasSubPermission("highlight-entities")))
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
                                    DoylCraft.instance(),
                                    () -> entities.forEach(entity -> entity.setGlowing(false)),
                                    ctx.getArgument("duration", Integer.class)
                                );
                            }
                        }))
                    )
                )
            );
    }

    private static Predicate<CommandSourceStack> hasSubPermission(String permission) {
        return hasPermission("doylcraft.command.utils." + permission);
    }

}
