package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;

public class AFKEvent implements Listener {

    DiscordHandler discordHandler;

    public AFKEvent(DiscordHandler discordHandler) {
        this.discordHandler = discordHandler;
    }

    @EventHandler
    public void onAFKStateChange(AfkStatusChangeEvent event) {
        Player affected = event.getAffected().getBase();
        if(event.getValue()) {
            discordHandler.sendDiscordEmbed("global", new EmbedBuilder().setAuthor(affected.getName() + " is now AFK", null, DiscordSRV.getAvatarUrl(affected)).setColor(Color.decode("#ff0000")));
        } else {
            discordHandler.sendDiscordEmbed("global", new EmbedBuilder().setAuthor(affected.getName() + " is no longer AFK", null, DiscordSRV.getAvatarUrl(affected)).setColor(Color.decode("#00ff00")));
        }
    }

}
