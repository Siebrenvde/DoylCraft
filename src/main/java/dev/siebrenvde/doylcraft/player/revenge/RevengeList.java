package dev.siebrenvde.doylcraft.player.revenge;

import dev.siebrenvde.doylcraft.DoylCraft;
import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class RevengeList {

    private final Map<UUID, BukkitTask> entities = new HashMap<>();

    public boolean contains(Entity entity) {
        return entities.containsKey(entity.getUniqueId());
    }

    public void add(Entity entity) {
        remove(entity);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(
            DoylCraft.instance(),
            () -> entities.remove(entity.getUniqueId()),
            Tick.tick().fromDuration(Duration.ofMinutes(1)) // Give players a minute to take revenge
        );
        entities.put(entity.getUniqueId(), task);
    }

    public void remove(Entity entity) {
        Optional.ofNullable(entities.remove(entity.getUniqueId())).ifPresent(BukkitTask::cancel);
    }

    public void cancelAll() {
        entities.values().forEach(BukkitTask::cancel);
    }

}
