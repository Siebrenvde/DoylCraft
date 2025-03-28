package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MenuUtils {

    public static Component forLore(Component component) {
        return component
            .colorIfAbsent(NamedTextColor.WHITE)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.byBoolean(false));
    }

}
