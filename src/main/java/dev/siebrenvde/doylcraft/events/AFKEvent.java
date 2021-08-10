package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;

public class AFKEvent implements Listener {

    DiscordHandler handler;

    public AFKEvent(Main main) {
        handler = main.getDiscordHandler();
    }

    @EventHandler
    public boolean onAFKStateChange(AfkStatusChangeEvent event) {
        Player affected = event.getAffected().getBase();
        if(event.getValue()) {
            handler.sendDiscordEmbed("global", new EmbedBuilder().setAuthor(affected.getName() + " is now AFK.", null, "https://crafatar.com/avatars/" + affected.getUniqueId() + "?overlay").setColor(Color.decode("#ff0000")));
        } else {
            handler.sendDiscordEmbed("global", new EmbedBuilder().setAuthor(affected.getName() + " is no longer AFK.", null, "https://crafatar.com/avatars/" + affected.getUniqueId() + "?overlay").setColor(Color.decode("#00ff00")));
        }
        return true;
    }

}
