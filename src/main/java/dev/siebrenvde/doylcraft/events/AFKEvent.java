package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;

public class AFKEvent implements Listener {

    DiscordSRVAddon discordSRVAddon;

    public AFKEvent(DiscordSRVAddon discordSRVAddon) {
        this.discordSRVAddon = discordSRVAddon;
    }

    @EventHandler
    public void onAFKStateChange(AfkStatusChangeEvent event) {
        Player affected = event.getAffected().getBase();
        if(event.getValue()) {
            discordSRVAddon.sendDiscordEmbed("global", new EmbedBuilder().setAuthor(affected.getName() + " is now AFK", null, DiscordSRV.getAvatarUrl(affected)).setColor(Color.decode("#ff0000")));
        } else {
            discordSRVAddon.sendDiscordEmbed("global", new EmbedBuilder().setAuthor(affected.getName() + " is no longer AFK", null, DiscordSRV.getAvatarUrl(affected)).setColor(Color.decode("#00ff00")));
        }
    }

}
