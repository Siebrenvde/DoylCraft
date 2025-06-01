package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.preferences.PlayerPreferences;
import dev.siebrenvde.doylcraft.player.PlayerData;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;

import static io.papermc.paper.datacomponent.DataComponentTypes.DAMAGE;
import static io.papermc.paper.datacomponent.DataComponentTypes.MAX_DAMAGE;
import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

/**
 * Listener for {@link PlayerItemDamageEvent}
 * <p>
 * Warns players when an item reaches a low durability level
 */
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class ItemDamageListener implements Listener {

    /**
     * The key for the durability ping cooldown
     */
    public static final NamespacedKey LAST_PING_KEY = new NamespacedKey(
        DoylCraft.instance(),
        "last_durability_ping"
    );

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onItemDamage(PlayerItemDamageEvent event) {
        PlayerPreferences.PrefDurabilityPing prefs = PlayerData.preferences(event.getPlayer()).durabilityPing;
        if (!prefs.enabled()) return;

        ItemStack item = event.getItem();
        int damage = requireNonNull(item.getData(DAMAGE)) + event.getDamage();
        int maxDamage = requireNonNull(item.getData(MAX_DAMAGE));

        int pingDamage = maxDamage - ((maxDamage * prefs.percentage()) / 100);
        if (damage < pingDamage || damage >= maxDamage) return;

        PersistentDataContainerView container = item.getPersistentDataContainer();

        if (!container.has(LAST_PING_KEY, PersistentDataType.LONG)) {
            pingPlayer(event.getPlayer(), item, damage, maxDamage);
            return;
        }

        Instant lastPing = Instant.ofEpochMilli(requireNonNull(container.get(LAST_PING_KEY, PersistentDataType.LONG)));
        if (Instant.now().isBefore(lastPing.plusSeconds(prefs.cooldown()))) return;

        pingPlayer(event.getPlayer(), item, damage, maxDamage);
    }

    private void pingPlayer(Player player, ItemStack item, int damage, int maxDamage) {
        item.editPersistentDataContainer(pdc -> pdc.set(LAST_PING_KEY, PersistentDataType.LONG, Instant.now().toEpochMilli()));
        player.sendActionBar(
            text()
                .append(Components.itemStack(item).colorIfAbsent(NamedTextColor.WHITE))
                .append(text(" has low durability! "))
                .append(text((maxDamage - damage) + "/" + maxDamage, NamedTextColor.WHITE))
                .append(text(" remaining"))
                .colorIfAbsent(NamedTextColor.RED)
        );
        player.playSound(
            player,
            Sound.BLOCK_ANVIL_LAND,
            0.3f,
            1
        );
    }

}
