package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.regex.Pattern;

public class ChatEvent implements Listener {

    private DiscordHandler handler;

    public ChatEvent(DiscordHandler discordHandler) {
        handler = discordHandler;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Component component = event.originalMessage();
        for(Member member : handler.getMembers()) {
            TextReplacementConfig config = TextReplacementConfig.builder()
                    .match(Pattern.compile("@" + member.getEffectiveName(), Pattern.CASE_INSENSITIVE))
                    .replacement(Component.text(ChatColor.BLUE + "@" + member.getEffectiveName() + ChatColor.RESET))
                    .build();
            component = component.replaceText(config);
        }
        event.message(component);
    }

}
