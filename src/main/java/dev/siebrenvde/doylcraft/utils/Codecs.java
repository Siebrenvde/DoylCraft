package dev.siebrenvde.doylcraft.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Codecs {

    public static final Codec<World> WORLD = UUIDUtil.STRING_CODEC.comapFlatMap(
        uuid -> {
            World world = Bukkit.getWorld(uuid);
            if (world == null) return DataResult.error(() -> "World with id " + uuid + " does not exist");
            return DataResult.success(world);
        },
        World::getUID
    );

    public static final Codec<Location> LOCATION = RecordCodecBuilder.create(instance -> instance.group(
        WORLD.fieldOf("world").forGetter(Location::getWorld),
        Codec.DOUBLE.fieldOf("x").forGetter(Location::getX),
        Codec.DOUBLE.fieldOf("y").forGetter(Location::getY),
        Codec.DOUBLE.fieldOf("z").forGetter(Location::getZ),
        Codec.FLOAT.fieldOf("yaw").forGetter(Location::getYaw),
        Codec.FLOAT.fieldOf("pitch").forGetter(Location::getPitch)
    ).apply(instance, Location::new));

}
