package dev.siebrenvde.doylcraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.siebrenvde.doylcraft.location.NamedLocation;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@NullMarked
abstract class NamedLocationArgumentType<T extends NamedLocation> implements CustomArgumentType<T, String> {

    protected abstract List<T> locations(CommandSender sender);

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        locations(((CommandSourceStack) context.getSource()).getSender()).forEach(location -> {
            if (location.key().startsWith(builder.getRemainingLowerCase())) builder.suggest(
                location.key(),
                MessageComponentSerializer.message().serialize(location.displayName())
            );
        });
        return builder.buildFuture();
    }

}
