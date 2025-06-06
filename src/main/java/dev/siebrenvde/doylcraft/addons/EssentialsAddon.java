package dev.siebrenvde.doylcraft.addons;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class EssentialsAddon {

    //private static final EssentialsAddon instance = new EssentialsAddon();
    private static final IEssentials essentials;

    /*private EssentialsAddon() {
        essentials = (IEssentials) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Essentials"));
    }*/

    public static IUser getUser(Player player) {
        return essentials.getUser(player);
    }

    public static CompletableFuture<Boolean> teleport(Player player, Location location, TeleportCause cause) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getUser(player).getAsyncTeleport().teleport(location, null, cause, future);
        return future;
    }

    static {
        essentials = (IEssentials) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Essentials"));
    }

}
