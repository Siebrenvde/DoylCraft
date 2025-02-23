package dev.siebrenvde.doylcraft.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link CustomArgumentType} that returns an {@link OfflinePlayer} and suggests all online players
 */
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class OfflinePlayerArgumentType implements CustomArgumentType<OfflinePlayer, String> {

    public static OfflinePlayerArgumentType offlinePlayer() {
        return new OfflinePlayerArgumentType();
    }

    @Override
    public OfflinePlayer parse(StringReader stringReader) throws CommandSyntaxException {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(stringReader.readString());
        if (offlinePlayer == null) {
            throw new SimpleCommandExceptionType(new LiteralMessage("No player was found")).create();
        }
        return offlinePlayer;
    }

    @Override
    public StringArgumentType getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

}
