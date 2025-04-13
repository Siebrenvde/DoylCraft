package dev.siebrenvde.doylcraft.location;

import dev.siebrenvde.doylcraft.menu.AbstractMenu;
import dev.siebrenvde.doylcraft.menu.MenuUtils;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static dev.siebrenvde.doylcraft.menu.MenuUtils.forLore;
import static io.papermc.paper.datacomponent.DataComponentTypes.ITEM_NAME;
import static io.papermc.paper.datacomponent.DataComponentTypes.LORE;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class LocationListMenu<T extends NamedLocation> extends AbstractMenu {

    private final List<T> locations;

    protected LocationListMenu(Player player, List<T> locations, Component title) {
        super(player, locations.size(), title);
        this.locations = locations;

        fillInventory(
            locations.stream().map(location -> {
                ItemStack stack = location.icon().createItemStack();
                stack.setData(ITEM_NAME, location.displayName());
                stack.setData(LORE, ItemLore.lore()
                    .addLines(location.info().stream().map(MenuUtils::forLore).toList())
                    .addLine(forLore(text("Click to teleport", NamedTextColor.AQUA)))
                    .build()
                );
                return stack;
            }).toList()
        );
    }

    protected abstract String command(T location);

    @Override
    protected void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot >= locations.size()) return;
        player.performCommand(command(locations.get(slot)));
    }

}
