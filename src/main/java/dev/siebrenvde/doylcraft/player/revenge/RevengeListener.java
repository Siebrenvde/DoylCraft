package dev.siebrenvde.doylcraft.player.revenge;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public record RevengeListener(RevengeList list, Entity entity) implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onEntityDeath(EntityDeathEvent event) {
        if (!event.getEntity().equals(entity)) return;
        list.remove(entity);

        if (!(event.getDamageSource().getCausingEntity() instanceof Player player)) return;
        if (!player.equals(list.player())) return;

        Component message = text()
            .append(Components.entity(player))
            .append(text(" got revenge on "))
            .append(Components.entity(entity))
            .color(NamedTextColor.GRAY)
            .build();

        Bukkit.broadcast(message);
        DiscordSRVAddon.sendEmbed(
            DiscordSRVAddon.GLOBAL_CHANNEL,
            DiscordSRVAddon.playerEmbed(player, message).setColor(NamedTextColor.GRAY.value())
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDespawn(EntityRemoveEvent event) {
        if (event.getEntity().equals(entity) && event.getCause() != EntityRemoveEvent.Cause.UNLOAD) {
            list.remove(entity);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().equals(entity)) return;
        if (!(event.getDamager() instanceof Player)) return;
        list.resetTimeout(entity);
    }

}
