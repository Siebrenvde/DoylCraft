package dev.siebrenvde.doylcraft.player.preferences.menu.option;

import dev.siebrenvde.doylcraft.player.preferences.menu.PreferencesMenu;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.quiltmc.config.api.values.TrackedValue;

import static dev.siebrenvde.doylcraft.utils.MenuUtils.forLore;
import static io.papermc.paper.datacomponent.DataComponentTypes.ITEM_NAME;
import static io.papermc.paper.datacomponent.DataComponentTypes.LORE;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class MenuOption {

    protected final ItemStack stack;
    protected final Component name;
    protected final @Nullable Component description;
    protected final @Nullable Component defaultLore;
    protected final Player player;

    MenuOption(Material material, Component name, @Nullable Component description, @Nullable Component defaultLore, Player player) {
        this.stack = ItemStack.of(material);
        this.name = name;
        this.description = description;
        this.defaultLore = defaultLore;
        this.player = player;
        stack.setData(ITEM_NAME, name.colorIfAbsent(NamedTextColor.WHITE));
        setLore();
    }

    public static Builder option(Material material, Component name, Player player) {
        return new Builder(material, name, player);
    }

    /**
     * The click handler for the option
     * @return whether to update the options
     */
    public abstract boolean onClick();

    protected void setLore(Component... lore) {
        ItemLore.Builder builder = ItemLore.lore();
        for (Component line : lore) {
            builder.addLine(forLore(line));
        }
        if (description != null) builder.addLine(forLore(description.colorIfAbsent(NamedTextColor.GRAY)));
        if (defaultLore != null) builder.addLine(forLore(defaultLore));
        stack.setData(LORE, builder.build());
    }

    public ItemStack itemStack() {
        return stack;
    }

    public static class Builder {

        private final Material material;
        private final Component name;
        private @Nullable Component description;
        private final Player player;

        private Builder(Material material, Component name, Player player) {
            this.material = material;
            this.name = name;
            this.player = player;
        }

        public Builder description(Component description) {
            this.description = description;
            return this;
        }

        public Toggleable toggleable(TrackedValue<Boolean> preference) {
            return new Toggleable(material, name, description, player, preference);
        }

        public Navigation navigation(Class<? extends PreferencesMenu> menuClass) {
            return new Navigation(material, name, description, player, menuClass);
        }

        public CommandInput commandInput(Component valueDisplay, String command) {
            return new CommandInput(material, name, description, player, valueDisplay, command);
        }

    }

}
