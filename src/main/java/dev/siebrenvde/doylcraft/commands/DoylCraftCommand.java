package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.ConfigSubCommand;
import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.DebugSubCommand;
import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.PreferencesSubCommand;
import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.UtilsSubCommand;
import dev.siebrenvde.doylcraft.commands.subcommands.doylcraft.VersionSubCommand;
import dev.siebrenvde.doylcraft.utils.BuildParameters;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@NullMarked
public class DoylCraftCommand extends CommandBase {

    private static final String GITHUB_URL = "https://github.com/Siebrenvde/DoylCraft";

    public static void register(Commands commands) {
        commands.register(
            literal("doylcraft")
                .executes(ctx -> {
                    TextComponent.Builder builder = Component.text();
                    builder.append(
                        text("DoylCraft")
                            .hoverEvent(HoverEvent.showText(text(GITHUB_URL)))
                            .clickEvent(ClickEvent.openUrl(GITHUB_URL)),
                        text(" version "),
                        text(BuildParameters.VERSION, NamedTextColor.YELLOW)
                    );

                    if (!BuildParameters.IS_CI) {
                        builder.appendSpace();
                        builder.append(text("(DEV)", NamedTextColor.RED));
                    }

                    builder.appendSpace();
                    builder.append(
                        text()
                            .append(text("("))
                            .append(text(BuildParameters.GIT_BRANCH, NamedTextColor.AQUA))
                            .append(text("@"))
                            .append(
                                text(BuildParameters.GIT_COMMIT_HASH.substring(0, 7), NamedTextColor.GREEN)
                                    .hoverEvent(HoverEvent.showText(
                                        text()
                                            .append(text(BuildParameters.GIT_COMMIT_MESSAGE, NamedTextColor.GREEN))
                                            .appendNewline()
                                            .append(text(BuildParameters.GIT_COMMIT_AUTHOR, NamedTextColor.YELLOW))
                                    ))
                            )
                            .append(text(")"))
                            .clickEvent(ClickEvent.openUrl(GITHUB_URL + "/commit/" + BuildParameters.GIT_COMMIT_HASH))
                    );

                    builder.color(NamedTextColor.WHITE);
                    ctx.getSource().getSender().sendMessage(builder);
                    return SINGLE_SUCCESS;
                })
                .then(PreferencesSubCommand.get())
                .then(DebugSubCommand.get())
                .then(UtilsSubCommand.get())
                .then(VersionSubCommand.get())
                .then(ConfigSubCommand.get())
                .build(),
            "The DoylCraft command"
        );
    }

}
