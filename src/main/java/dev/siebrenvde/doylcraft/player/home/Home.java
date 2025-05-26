package dev.siebrenvde.doylcraft.player.home;

import com.mojang.serialization.Codec;
import dev.siebrenvde.doylcraft.location.NamedLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public final class Home extends NamedLocation {

    public static final Codec<Home> CODEC = createCodec(Home::new);

    public Home(String key, Location location, Component displayName) {
        this(key, location, displayName, DEFAULT_ICON);
    }

    public Home(String key, Location location, Component displayName, ItemType icon) {
        super(key, location, displayName.colorIfAbsent(NamedTextColor.WHITE), icon);
    }

    @Override
    public Component asComponent() {
        return super.asComponent().clickEvent(ClickEvent.suggestCommand("/home " + key));
    }

}
