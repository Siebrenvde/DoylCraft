package dev.siebrenvde.doylcraft.player.revenge;

import dev.siebrenvde.doylcraft.DoylCraft;
import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class RevengeList {

    private static final int TIMEOUT_TICKS = Tick.tick().fromDuration(Duration.ofMinutes(5));

    private final Player player;
    private final Map<UUID, Entry> entities = new HashMap<>();

    public RevengeList(Player player) {
        this.player = player;
    }

    public Player player() {
        return player;
    }

    public boolean contains(Entity entity) {
        return entities.containsKey(entity.getUniqueId());
    }

    public void add(Entity entity) {
        if (contains(entity)) {
            resetTimeout(entity);
            return;
        }
        entities.put(entity.getUniqueId(), new Entry(entity, this));
    }

    public void remove(Entity entity) {
        Optional.ofNullable(entities.remove(entity.getUniqueId())).ifPresent(Entry::cancel);
    }

    public void resetTimeout(Entity entity) {
        Optional.ofNullable(entities.get(entity.getUniqueId())).ifPresent(Entry::resetTimeout);
    }

    public void cancelAll() {
        entities.values().forEach(Entry::cancel);
    }

    private BukkitTask createTask(Runnable runnable) {
        return Bukkit.getScheduler().runTaskLater(DoylCraft.instance(), runnable, TIMEOUT_TICKS);
    }

    private final class Entry {

        private final Runnable runnable;
        private final RevengeListener listener;
        private BukkitTask task;

        private Entry(Entity entity, RevengeList list) {
            this.runnable = () -> remove(entity);
            this.listener = new RevengeListener(list, entity);
            this.task = createTask(this.runnable);
            Bukkit.getPluginManager().registerEvents(this.listener, DoylCraft.instance());
        }

        public void cancel() {
            task.cancel();
            EntityRemoveEvent.getHandlerList().unregister(listener);
        }

        public void resetTimeout() {
            task.cancel();
            task = createTask(runnable);
        }

    }

}
