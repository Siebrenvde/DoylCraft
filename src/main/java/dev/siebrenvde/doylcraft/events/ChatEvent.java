package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.utils.Colours;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.regex.Pattern;

public class ChatEvent implements Listener {

    private DiscordHandler discordHandler;

    public ChatEvent(DiscordHandler discordHandler) {
        this.discordHandler = discordHandler;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Component component = event.originalMessage();
        if(((TextComponent)component).content().contains("@")) {
            for(Member member : discordHandler.getMembers()) {
                TextReplacementConfig config = TextReplacementConfig.builder()
                    .match(Pattern.compile("@" + member.getEffectiveName(), Pattern.CASE_INSENSITIVE))
                    .replacement(profileComponent(member, component))
                    .build();
                component = component.replaceText(config);
            }
            event.message(component);
        }
    }

    private Component profileComponent(Member member, Component message) {
        if(((TextComponent)message).content().toLowerCase().contains("@" + member.getEffectiveName().toLowerCase())) {

            Component display = Component.text("@" + member.getEffectiveName(), Colours.DISCORD);

            Component profile = Component.text(member.getEffectiveName(), TextColor.color(member.getColorRaw()));

            if(!member.getEffectiveName().equals(member.getUser().getAsTag())) {
                profile = profile.append(Component.newline().append(Component.text(member.getUser().getAsTag(), NamedTextColor.WHITE)));
            }

            int roleAmount = member.getRoles().size();
            for(int i = 0; i < roleAmount; i++) {
                if(i == 0) {
                    profile = profile.append(Component.newline());
                }
                Role role = member.getRoles().get(i);
                profile = profile.append(Component.text(role.getName(), TextColor.color(role.getColorRaw())));
                if(i != (roleAmount - 1)) {
                    profile = profile.append(Component.text(" | ", NamedTextColor.GRAY));
                }
            }

            return display.hoverEvent(HoverEvent.showText(profile));
        }

        return Component.empty();
    }

}
