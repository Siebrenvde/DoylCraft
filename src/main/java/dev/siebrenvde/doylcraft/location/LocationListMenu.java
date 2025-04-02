package dev.siebrenvde.doylcraft.location;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.utils.MenuUtils;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static dev.siebrenvde.doylcraft.utils.MenuUtils.forLore;
import static io.papermc.paper.datacomponent.DataComponentTypes.ITEM_NAME;
import static io.papermc.paper.datacomponent.DataComponentTypes.LORE;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class LocationListMenu<T extends NamedLocation> implements InventoryHolder {

    private final Player player;
    private final List<T> locations;
    private final Inventory inventory;

    protected LocationListMenu(Player player, List<T> locations) {
        this.player = player;
        this.locations = locations;
        this.inventory = Bukkit.createInventory(
            this,
            9 * (int) Math.ceil((double) locations.size() / 9),
            title()
        );

        int i = 0;
        for (T location: locations) {
            ItemStack stack = location.icon().createItemStack();
            stack.setData(ITEM_NAME, location.displayName());
            stack.setData(LORE, ItemLore.lore()
                .addLines(location.info().stream().map(MenuUtils::forLore).toList())
                .addLine(forLore(text("Click to teleport", NamedTextColor.AQUA)))
                .build()
            );
            inventory.setItem(i, stack);
            i++;
        }

        Bukkit.getPluginManager().registerEvents(new Listeners(), DoylCraft.instance());
        player.openInventory(inventory);
    }

    protected abstract Component title();

    protected abstract String command(T location);

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private class Listeners implements Listener {

        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory() != inventory) return;
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) event.setCancelled(true);
            if (event.getClickedInventory() != inventory) return;
            event.setCancelled(true);
            int slot = event.getSlot();
            if (slot >= locations.size()) return;
            player.performCommand(command(locations.get(slot)));
        }

        @EventHandler
        private void onInventoryDrag(InventoryDragEvent event) {
            if (event.getInventory() != inventory) return;
            if (event.getRawSlots().stream().noneMatch(i -> i < inventory.getSize())) return;
            event.setCancelled(true);
        }

        @EventHandler
        private void onInventoryClose(InventoryCloseEvent event) {
            if (event.getInventory() != inventory) return;
            HandlerList.unregisterAll(this);
        }

    }

}
