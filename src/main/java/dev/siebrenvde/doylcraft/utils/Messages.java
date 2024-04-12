package dev.siebrenvde.doylcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;

public class Messages {

    public static final TextComponent PLAYER_ONLY = Component.text("Only players can execute this command.", Colours.ERROR);
    public static final TextComponent NO_PERMISSION = Component.text("You don't have permission to use this command.", Colours.ERROR);

    public static TextComponent playerNotFound(String name) {
        return Component.text("Player ", Colours.GENERIC)
        .append(Component.text(name, Colours.DATA))
        .append(Component.text(" does not exist or has not joined the server before.", Colours.GENERIC));
    }

    public static TextComponent error(TextComponent message, Exception e) {
        if(e != null) {
            TextComponent tc = Component.text(e.getClass().getSimpleName(), Colours.ERROR);
            if(e.getMessage() != null) {
                tc = tc.append(Component.newline());
                tc = tc.append(Component.text(e.getMessage(), Colours.DATA));
            }
            message = message.hoverEvent(HoverEvent.showText(tc));
        }
        return message;
    }

    public static TextComponent error(String message, Exception e) { return error(Component.text(message, Colours.ERROR), e); }
    public static TextComponent error(TextComponent message) { return error(message, null); }
    public static TextComponent error(String message) { return error(message, null); }

    public static TextComponent usage(String usage) {
        return Component.text("Usage: " + usage, Colours.ERROR);
    }

}
