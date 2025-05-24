package dev.siebrenvde.doylcraft.commands.subcommands.doylcraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.siebrenvde.doylcraft.config.Config;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jspecify.annotations.NullMarked;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class ConfigSubCommand extends CommandBase {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return literal("config")
            .requires(hasPermission("doylcraft.command.config"))
            .then(literal("reload")
                .executes(ctx -> {
                    Config.reload();
                    ctx.getSource().getSender().sendMessage(text("Reloaded config", Colours.GENERIC));
                    return 1;
                })
            );
    }

}
