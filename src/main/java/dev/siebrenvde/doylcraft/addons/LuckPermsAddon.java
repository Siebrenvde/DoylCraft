package dev.siebrenvde.doylcraft.addons;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

@NullMarked
public class LuckPermsAddon {

    private static final LuckPerms luckPerms;

    /**
     * {@return a set of all groups}
     */
    public static Set<Group> getGroups() {
        return luckPerms.getGroupManager().getLoadedGroups();
    }

    /**
     * {@return the group of the provided player}
     * @param player the player
     */
    public static CompletableFuture<Optional<String>> getPlayerGroup(OfflinePlayer player) {
        return luckPerms.getUserManager().loadUser(player.getUniqueId())
            .thenApplyAsync(user -> {
                return user.getNodes(NodeType.INHERITANCE).stream()
                    .map(InheritanceNode::getGroupName)
                    .findFirst();
            });
    }

    /**
     * Set a player's group
     * @param player the player
     * @param groupName the name of the group
     */
    public static void setPlayerGroup(OfflinePlayer player, String groupName) {
        Group group = requireNonNull(luckPerms.getGroupManager().getGroup(groupName));
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
            user.data().clear(NodeType.INHERITANCE::matches);
            user.data().add(InheritanceNode.builder(group).build());
        });
    }

    /**
     * {@return whether the provided group exists}
     * @param group the group name to check
     */
    public static boolean groupExists(String group) {
        return luckPerms.getGroupManager().getGroup(group) != null;
    }

    static {
        luckPerms = requireNonNull(Bukkit.getServicesManager().load(LuckPerms.class));
    }

}
