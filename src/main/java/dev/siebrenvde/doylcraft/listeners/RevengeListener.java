package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.player.revenge.RevengeList;
import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class RevengeListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerDeath(PlayerDeathEvent event) {
        Entity entity = event.getDamageSource().getCausingEntity();
        if (entity == null || entity instanceof Player) return;
        PlayerData.revengeList(event.getPlayer()).add(entity);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getDamageSource().getCausingEntity() instanceof Player player)) return;

        Entity entity = event.getEntity();
        RevengeList list = PlayerData.revengeList(player);
        if (!list.contains(entity)) return;

        list.remove(entity);

        Component message = text()
            .append(Components.entity(player))
            .append(text(" took revenge on "))
            .append(Components.entity(entity))
            .color(NamedTextColor.GRAY)
            .build();

        Bukkit.broadcast(message);
        DiscordSRVAddon.get().sendEmbed(
            DiscordSRVAddon.GLOBAL_CHANNEL,
            DiscordSRVAddon.playerEmbed(player, message).setColor(NamedTextColor.GRAY.value())
        );
    }

}
