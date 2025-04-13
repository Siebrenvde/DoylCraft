package dev.siebrenvde.doylcraft.commands.subcommands.doylcraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.utils.BuildParameters;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class VersionSubCommand extends CommandBase {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return literal("version").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            ZoneId zoneId = sender instanceof Player player
                ? PlayerData.preferences(player).timezone()
                : ZoneId.from(ZoneOffset.UTC);
            sender.sendMessage(
                text()
                    .append(text("Version: "))
                    .append(text(BuildParameters.VERSION, Colours.DATA))
                    .appendNewline()
                    .append(text("ConfigLib Version: "))
                    .append(text(BuildParameters.CONFIGLIB_VERSION, Colours.DATA))
                    .appendNewline()
                    .append(text("Build Time (" + zoneId + "): "))
                    .append(
                        Components.timestamp(BuildParameters.BUILD_TIME, zoneId)
                            .color(Colours.DATA)
                            .hoverEvent(HoverEvent.showText(
                                Components.duration(Duration.between(
                                    BuildParameters.BUILD_TIME,
                                    Instant.now()
                                )).append(text(" ago"))
                            ))
                    )
                    .appendNewline()
                    .append(text("Is CI Build: "))
                    .append(Components.bool(BuildParameters.IS_CI))
                    .appendNewline()
                    .append(text("Git Branch: "))
                    .append(text(BuildParameters.GIT_BRANCH, Colours.DATA))
                    .appendNewline()
                    .append(text("Commit Hash: "))
                    .append(text(BuildParameters.GIT_COMMIT_HASH.substring(0, 7), Colours.DATA))
                    .appendNewline()
                    .append(text("Commit Message: "))
                    .append(text(BuildParameters.GIT_COMMIT_MESSAGE, Colours.DATA))
                    .appendNewline()
                    .append(text("Commit Author: "))
                    .append(text(BuildParameters.GIT_COMMIT_AUTHOR, Colours.DATA))
                    .color(Colours.GENERIC)
            );

            return 1;
        });
    }

}
