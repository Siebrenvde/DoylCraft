package dev.siebrenvde.doylcraft.warp;

import com.mojang.serialization.Codec;
import dev.siebrenvde.doylcraft.location.NamedLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Warp extends NamedLocation {

    public static final Codec<Warp> CODEC = createCodec(Warp::new);

    public Warp(String key, Location location, Component displayName) {
        this(key, location, displayName, DEFAULT_ICON);
    }

    public Warp(String key, Location location, Component displayName, ItemType icon) {
        super(key, location, displayName.colorIfAbsent(NamedTextColor.WHITE), icon);
    }

    @Override
    public Component asComponent() {
        return super.asComponent().clickEvent(ClickEvent.suggestCommand("/warp " + key));
    }

    public Warp copy() {
        return new Warp(key, location.clone(), displayName, icon);
    }

}
