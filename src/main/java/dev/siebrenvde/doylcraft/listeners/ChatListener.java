package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.utils.Colours;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

/**
 * Listener for {@link AsyncChatEvent}
 * <p>
 * Colours mentions of Discord members in blurple and adds a hover component with member info
 */
@NullMarked
public class ChatListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        Component component = event.originalMessage();
        String messageContent = PlainTextComponentSerializer.plainText().serialize(component);

        // Chat message must contain '@'
        if(!messageContent.contains("@")) { return; }


        for(Member member : DiscordSRVAddon.get().getMembers()) {
            // Message must contain effective name of member
            if(!messageContent.toLowerCase().contains("@" + member.getEffectiveName().toLowerCase())) { continue; }

            component = component.replaceText(TextReplacementConfig.builder()
                .match(Pattern.compile(Pattern.quote("@" + member.getEffectiveName()), Pattern.CASE_INSENSITIVE))
                .replacement(profileComponent(member))
                .build()
            );
        }

        event.message(component);
    }

    private Component profileComponent(Member member) {
        TextComponent.Builder profile = text()
            .append(text(member.getEffectiveName(), TextColor.color(member.getColorRaw())));

        if(!member.getEffectiveName().equals(member.getUser().getAsTag())) {
            profile.appendNewline();
            profile.append(text(member.getUser().getAsTag(), NamedTextColor.WHITE));
        }

        if(!member.getRoles().isEmpty()) {
            profile.appendNewline();
            profile.append(Component.join(
                JoinConfiguration.separator(text(" | ", NamedTextColor.GRAY)),
                member.getRoles().stream()
                    .map(role -> text(role.getName(), TextColor.color(role.getColorRaw())))
                    .toList()
            ));
        }

        return text()
            .content("@" + member.getEffectiveName())
            .color(Colours.DISCORD)
            .hoverEvent(HoverEvent.showText(profile))
            .build();
    }

}
