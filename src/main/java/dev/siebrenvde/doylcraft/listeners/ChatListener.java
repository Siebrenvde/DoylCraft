package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Pattern;

/**
 * Listener for {@link AsyncChatEvent}
 * <p>
 * Colours mentions of Discord members in blurple and adds a hover component with member info
 */
public class ChatListener implements Listener {

    private final DiscordSRVAddon discordSRVAddon;

    public ChatListener(DiscordSRVAddon discordSRVAddon) {
        this.discordSRVAddon = discordSRVAddon;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        Component component = event.originalMessage();
        String messageContent = ((TextComponent)component).content();

        // Chat message must contain '@'
        if(!messageContent.contains("@")) { return; }

        for(Member member : discordSRVAddon.getMembers()) {
            // Message must contain effective name of member
            if(!messageContent.toLowerCase().contains(member.getEffectiveName().toLowerCase())) { continue; }

            TextReplacementConfig config = TextReplacementConfig.builder()
                .match(Pattern.compile(Pattern.quote("@" + member.getEffectiveName()), Pattern.CASE_INSENSITIVE))
                .replacement(profileComponent(member))
                .build();
            component = component.replaceText(config);
        }

        event.message(component);
    }

    private Component profileComponent(Member member) {
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

}
