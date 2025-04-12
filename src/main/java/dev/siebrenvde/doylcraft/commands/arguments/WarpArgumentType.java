package dev.siebrenvde.doylcraft.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.siebrenvde.doylcraft.warp.Warp;
import dev.siebrenvde.doylcraft.warp.Warps;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class WarpArgumentType extends NamedLocationArgumentType<Warp> {

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
    protected List<Warp> locations(CommandSender sender) {
        return Warps.WARPS.values().stream().toList();
    }

}
