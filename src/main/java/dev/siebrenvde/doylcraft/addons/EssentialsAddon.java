package dev.siebrenvde.doylcraft.addons;

import com.earth2me.essentials.commands.WarpNotFoundException;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.ess3.api.IWarps;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class EssentialsAddon {

    private static final EssentialsAddon instance = new EssentialsAddon();
    private final IEssentials essentials;

    private EssentialsAddon() {
        essentials = (IEssentials) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Essentials"));
    }

    public static IUser getUser(Player player) {
        return instance.essentials.getUser(player);
    }

    public static CompletableFuture<Boolean> teleport(Player player, Location location, TeleportCause cause) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getUser(player).getAsyncTeleport().teleport(location, null, cause, future);
        return future;
    }

    public static Map<String, Location> getWarps() {
        IWarps warps = (IWarps) instance.essentials.getWarps();
        Map<String, Location> result = new HashMap<>();
        for (String warp : warps.getList()) {
            try {
                result.put(warp, warps.getWarp(warp));
            } catch (WarpNotFoundException | InvalidWorldException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
