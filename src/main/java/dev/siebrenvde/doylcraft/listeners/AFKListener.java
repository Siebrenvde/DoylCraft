package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.awt.Color;

/**
 * Listener for Essentials' {@link AfkStatusChangeEvent}
 * <p>
 * Sends an embed to the Discord game chat channel when a player goes AFK or returns from being AFK
 */
@NullMarked
public class AFKListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAFKStateChange(AfkStatusChangeEvent event) {
        Player affected = event.getAffected().getBase();
        DiscordSRVAddon.get().sendEmbed(
            DiscordSRVAddon.GLOBAL_CHANNEL,
            new EmbedBuilder()
                .setAuthor(
                    affected.getName() + (event.getValue() ? " is now AFK" : " is no longer AFK"),
                    null,
                    DiscordSRV.getAvatarUrl(affected)
                )
                .setColor(Color.decode(event.getValue() ? "#ff0000" : "#00ff00"))
        );
    }

}
