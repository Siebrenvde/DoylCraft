package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.preferences.PlayerPreferences;
import dev.siebrenvde.doylcraft.preferences.Preferences;
import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

/**
 * Listener for {@link PlayerItemDamageEvent}
 * <p>
 * Warns players when an item reaches a low duration level
 */
public class ItemDamageListener implements Listener {

    /**
     * The key for the durability ping cooldown
     */
    public static final NamespacedKey LAST_PING_KEY = new NamespacedKey(
        DoylCraft.getInstance(),
        "last_durability_ping"
    );

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onItemDamage(PlayerItemDamageEvent event) {
        PlayerPreferences.PrefDurabilityPing prefs = Preferences.get(event.getPlayer()).durabilityPing;
        if(!prefs.enabled()) return;

        ItemStack item = event.getItem();
        Damageable meta = (Damageable) item.getItemMeta();

        int damage = meta.getDamage() + event.getDamage();
        short maxDurability = item.getType().getMaxDurability();

        int pingDurability = maxDurability - ((maxDurability * prefs.percentage()) / 100);
        if(damage < pingDurability || damage == maxDurability) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(!container.has(LAST_PING_KEY, PersistentDataType.LONG)) {
            pingPlayer(event.getPlayer(), item, damage, maxDurability);
            return;
        }

        Instant lastPing = Instant.ofEpochMilli(Objects.requireNonNull(container.get(LAST_PING_KEY, PersistentDataType.LONG)));
        if(Instant.now().isBefore(lastPing.plusSeconds(prefs.cooldown()))) return;

        pingPlayer(event.getPlayer(), item, damage, maxDurability);
    }

    private void pingPlayer(Player player, ItemStack item, int damage, short maxDurability) {
        item.editPersistentDataContainer(pdc -> pdc.set(LAST_PING_KEY, PersistentDataType.LONG, Instant.now().toEpochMilli()));
        player.sendActionBar(
            text()
                .append(Components.itemStack(item).colorIfAbsent(NamedTextColor.WHITE))
                .append(text(" has low durability! "))
                .append(text((maxDurability - damage) + "/" + maxDurability, NamedTextColor.WHITE))
                .append(text(" remaining"))
                .colorIfAbsent(NamedTextColor.RED)
        );
        player.playSound(
            player,
            Sound.BLOCK_ANVIL_LAND,
            1,
            1
        );
    }

}
