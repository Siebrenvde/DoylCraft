package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class Colours {

    public static final TextColor GENERIC = NamedTextColor.WHITE;
    public static final TextColor DATA = NamedTextColor.YELLOW;

    public static final TextColor TWITCH = Objects.requireNonNull(TextColor.fromHexString("#6441a5"));
    public static final TextColor DISCORD = Objects.requireNonNull(TextColor.fromHexString("#5865f2"));

}
