package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.DebugSubCommand;
import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.PreferencesSubCommand;
import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.UtilsSubCommand;
import dev.siebrenvde.doylcraft.utils.BuildParameters;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class DoylCraftCommand extends CommandBase {

    private static final String GITHUB_URL = "https://github.com/Siebrenvde/DoylCraft";

    public static void register(Commands commands) {
        commands.register(
            literal("doylcraft")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(
                        text()
                            .append(
                                text("DoylCraft")
                                    .hoverEvent(HoverEvent.showText(text(GITHUB_URL)))
                                    .clickEvent(ClickEvent.openUrl(GITHUB_URL))
                            )
                            .append(text(" version "))
                            .append(text(BuildParameters.VERSION, NamedTextColor.YELLOW))
                            .appendSpace()
                            .append(
                                text()
                                    .append(text("("))
                                    .append(text(BuildParameters.GIT_BRANCH, NamedTextColor.AQUA))
                                    .append(text("@"))
                                    .append(text(BuildParameters.GIT_COMMIT.substring(0, 7), NamedTextColor.GREEN))
                                    .append(text(")"))
                                    .clickEvent(ClickEvent.openUrl(GITHUB_URL + "/commit/" + BuildParameters.GIT_COMMIT))
                            )
                    );
                    return SINGLE_SUCCESS;
                })
                .then(PreferencesSubCommand.get())
                .then(DebugSubCommand.get())
                .then(UtilsSubCommand.get())
                .build(),
            "The DoylCraft command"
        );
    }

}
