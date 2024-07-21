package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.utils.Colours;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;

import static net.kyori.adventure.text.Component.text;

public class DiscordSRVListener {

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {

        if(event.getAuthor().getId().equals("864703528496398356")) {

            String[] strings = event.getMessage().getContentRaw().split("\n");

            String streamer = strings[0].split(" ")[0].replace("\\", "");

            Bukkit.broadcast(
                text().color(Colours.TWITCH)
                    .append(text("[Twitch] "), text(streamer), text(" is now live!"))
                    .hoverEvent(HoverEvent.showText(text(strings[1]))) // Show title
                    .clickEvent(ClickEvent.openUrl(strings[2])) // Open stream on click
                    .build()
            );

        }

    }

}
