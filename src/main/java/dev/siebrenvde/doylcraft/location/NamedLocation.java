package dev.siebrenvde.doylcraft.location;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.siebrenvde.doylcraft.utils.Codecs;
import dev.siebrenvde.doylcraft.utils.Components;
import io.papermc.paper.adventure.AdventureCodecs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Location;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class NamedLocation implements ComponentLike {

    protected static final ItemType DEFAULT_ICON = ItemType.ENDER_PEARL;

    protected final String key;
    protected final Location location;
    protected Component displayName;
    protected ItemType icon;

    protected NamedLocation(String key, Location location, Component displayName, ItemType icon) {
        this.key = key;
        this.location = location;
        this.displayName = displayName;
        this.icon = icon;
    }

    @Override
    public Component asComponent() {
        return displayName().hoverEvent(join(JoinConfiguration.newlines(), info()));
    }

    /**
     * {@return a list of info components}
     * <br>
     * Contains the key, location and world
     */
    public List<Component> info() {
        return List.of(
            text(key(), NamedTextColor.YELLOW),
            Components.location(location()),
            text(location().getWorld().getName(), NamedTextColor.GRAY)
        );
    }

    /**
     * {@return the key}
     */
    public String key() {
        return key;
    }

    /**
     * {@return the location}
     */
    public Location location() {
        return location;
    }

    /**
     * {@return the display name}
     */
    public Component displayName() {
        return displayName;
    }

    /**
     * {@return the ItemType of the icon}
     */
    public ItemType icon() {
        return icon;
    }

    /**
     * Sets the display name
     * @param displayName the new display name
     */
    public void displayName(Component displayName) {
        this.displayName = displayName;
    }

    /**
     * Sets the icon
     * @param icon the new icon
     */
    public void icon(ItemType icon) {
        this.icon = icon;
    }

    protected static <T extends NamedLocation> Codec<T> createCodec(Class<T> clazz) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(NamedLocation::key),
            Codecs.LOCATION.fieldOf("location").forGetter(NamedLocation::location),
            AdventureCodecs.COMPONENT_CODEC.fieldOf("display_name").forGetter(NamedLocation::displayName),
            Codecs.ITEM_TYPE.optionalFieldOf("icon", DEFAULT_ICON).forGetter(NamedLocation::icon)
        ).apply(instance, (key, location, displayName, icon) -> {
            try {
                return clazz.getDeclaredConstructor(
                    String.class,
                    Location.class,
                    Component.class,
                    ItemType.class
                ).newInstance(key, location, displayName, icon);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public static final MiniMessage DISPLAY_NAME_MM = MiniMessage.builder()
        .tags(TagResolver.resolver(
            StandardTags.decorations(),
            StandardTags.color(),
            StandardTags.keybind(),
            StandardTags.translatable(),
            StandardTags.translatableFallback(),
            StandardTags.font(),
            StandardTags.gradient(),
            StandardTags.rainbow(),
            StandardTags.reset(),
            StandardTags.pride(),
            StandardTags.shadowColor()
        ))
        .build();

}
