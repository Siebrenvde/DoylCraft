package dev.siebrenvde.doylcraft.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.player.home.Home;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class HomeArgumentType extends NamedLocationArgumentType<Home> {

    public static HomeArgumentType home() {
        return new HomeArgumentType();
    }

    public static Home getHome(CommandContext<CommandSourceStack> ctx, String name) {
        return ctx.getArgument(name, Home.class);
    }

    @Override
    public Home parse(StringReader reader) throws CommandSyntaxException {
        throw new SimpleCommandExceptionType(new LiteralMessage("No command source")).create();
    }

    @Override
    public <S> Home parse(StringReader reader, S source) throws CommandSyntaxException {
        String key = reader.readUnquotedString();
        CommandSender sender = ((CommandSourceStack) source).getSender();
        if (!(sender instanceof Player player)) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Sender is not a player")).create();
        }
        Home home = PlayerData.homes(player).get(key);
        if (home == null) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Home '" + key + "' does not exist")).create();
        }
        return home;
    }

    @Override
    protected List<Home> locations(CommandSender sender) {
        if (!(sender instanceof Player player)) return List.of();
        return PlayerData.homes(player).asList();
    }

}
