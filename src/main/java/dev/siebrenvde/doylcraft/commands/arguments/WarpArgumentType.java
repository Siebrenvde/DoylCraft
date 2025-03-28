package dev.siebrenvde.doylcraft.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.warp.Warp;
import dev.siebrenvde.doylcraft.warp.Warps;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class WarpArgumentType implements CustomArgumentType<Warp, String> {

    public static WarpArgumentType warp() {
        return new WarpArgumentType();
    }

    public static Warp getWarp(CommandContext<?> ctx, String name) {
        return ctx.getArgument(name, Warp.class);
    }

    @Override
    public Warp parse(StringReader reader) throws CommandSyntaxException {
        String key = reader.readUnquotedString();
        Warp warp = Warps.WARPS.get(key);
        if (warp == null) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Warp '" + key + "' does not exist")).create();
        }
        return warp;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Warps.WARPS.values().forEach(warp -> {
            builder.suggest(
                warp.key(),
                MessageComponentSerializer.message().serialize(warp.displayName())
            );
        });
        return builder.buildFuture();
    }

}
