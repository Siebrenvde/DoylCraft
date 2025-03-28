package dev.siebrenvde.doylcraft.warp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.siebrenvde.doylcraft.utils.Codecs;
import io.papermc.paper.adventure.AdventureCodecs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public final class Warp implements ComponentLike {

    public static final Codec<Warp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("key").forGetter(Warp::key),
        AdventureCodecs.COMPONENT_CODEC.fieldOf("display_name").forGetter(Warp::displayName),
        Codecs.LOCATION.fieldOf("location").forGetter(Warp::location)
    ).apply(instance, Warp::new));

    private final String key;
    private Component displayName;
    private Location location;

    public Warp(String key, Component displayName, Location location) {
        this.key = key;
        this.displayName = displayName.colorIfAbsent(NamedTextColor.WHITE);
        this.location = location;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @NotNull Component asComponent() {
        return displayName.hoverEvent(
            text()
                .append(text(key, NamedTextColor.YELLOW))
                .appendNewline()
                .append(text(String.format("%s %s %s", location.blockX(), location.blockY(), location.blockZ()), NamedTextColor.GREEN))
                .appendNewline()
                .append(text(location.getWorld().getName(), NamedTextColor.GRAY))
                .build()
        ).clickEvent(ClickEvent.suggestCommand("/warp " + key));
    }

    public String key() {
        return key;
    }

    public Component displayName() {
        return displayName;
    }

    public void displayName(Component displayName) {
        this.displayName = displayName;
    }

    public Location location() {
        return location;
    }

    public void location(Location location) {
        this.location = location;
    }

    public Warp copy() {
        return new Warp(key, displayName, location.clone());
    }

}
