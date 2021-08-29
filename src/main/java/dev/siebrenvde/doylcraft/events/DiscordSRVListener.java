package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.utils.Utils;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import org.bukkit.Bukkit;

public class DiscordSRVListener {

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {

        if(event.getAuthor().getId().equals("856151903736954912")) {

            String[] strings = event.getMessage().getContentRaw().split("\n");

            String streamer = strings[0].split(" ")[0].replace("\\", "");

            if(streamer.equalsIgnoreCase("doyl_official")) {
                streamer = "Code";
            } else if(streamer.equalsIgnoreCase("Bitzbreaker_")) {
                streamer = "Bitzbreaker";
            }

            Utils.broadcastHover(streamer, strings[1], strings[2]);
            Bukkit.getLogger().info(streamer + ", " + strings[1] + ", " + strings[2]);

        }

    }

}
