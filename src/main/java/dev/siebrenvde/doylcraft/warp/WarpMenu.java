package dev.siebrenvde.doylcraft.warp;

import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.utils.MenuUtils;
import io.papermc.paper.datacomponent.item.ItemLore;
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

@NullMarked
public class WarpMenu implements InventoryHolder, Listener {

    private final Player player;
    private final List<Warp> warps;
    private final Inventory inventory;

    @SuppressWarnings("UnstableApiUsage")
    private WarpMenu(Player player) {
        this.player = player;
        warps = Warps.WARPS.values().stream().toList();
        inventory = Bukkit.createInventory(
            this,
            9 * (int) Math.ceil((double) warps.size() / 9),
            text("Warps")
        );

        int i = 0;
        for (Warp warp : warps) {
            ItemStack stack = warp.icon().createItemStack();
            stack.setData(ITEM_NAME, warp.displayName());
            stack.setData(LORE, ItemLore.lore()
                .addLines(warp.info().stream().map(MenuUtils::forLore).toList())
                .addLine(forLore(text("Click to teleport", NamedTextColor.AQUA)))
                .build()
            );
            inventory.setItem(i, stack);
            i++;
        }

        Bukkit.getPluginManager().registerEvents(this, DoylCraft.instance());
        player.openInventory(inventory);
    }

    public static void open(Player player) {
        new WarpMenu(player);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) return;
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) event.setCancelled(true);
        if (event.getClickedInventory() != inventory) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        if (slot >= warps.size()) return;
        player.performCommand("warp " + warps.get(slot).key());
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
