package dev.siebrenvde.doylcraft.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
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

    @SuppressWarnings("UnstableApiUsage")
    public static final Codec<ItemType> ITEM_TYPE = Item.CODEC.xmap(
        item -> CraftItemType.minecraftToBukkitNew(item.value()),
        material -> BuiltInRegistries.ITEM.wrapAsHolder(CraftItemType.bukkitToMinecraftNew(material))
    );

}
