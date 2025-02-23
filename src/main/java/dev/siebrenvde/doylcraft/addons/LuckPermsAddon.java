package dev.siebrenvde.doylcraft.addons;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@NullMarked
public class LuckPermsAddon {

    @Nullable private static LuckPermsAddon instance;
    private final LuckPerms luckPerms;

    public LuckPermsAddon() {
        instance = this;
        luckPerms = requireNonNull(Bukkit.getServicesManager().load(LuckPerms.class));
    }

    public static LuckPermsAddon get() {
        return requireNonNull(instance);
    }

    /**
     * {@return a set of all groups}
     */
    public Set<Group> getGroups() {
        return luckPerms.getGroupManager().getLoadedGroups();
    }

    /**
     * {@return the group of the provided player}
     * @param player the player
     */
    public CompletableFuture<@Nullable String> getPlayerGroup(OfflinePlayer player) {
        return luckPerms.getUserManager().loadUser(player.getUniqueId())
            .thenApplyAsync(user -> {
                Set<String> groups = user.getNodes(NodeType.INHERITANCE).stream()
                    .map(InheritanceNode::getGroupName)
                    .collect(Collectors.toSet());
                return groups.iterator().next();
            });
    }

    /**
     * Set a player's group
     * @param player the player
     * @param groupName the name of the group
     */
    public void setPlayerGroup(OfflinePlayer player, String groupName) {
        Group group = requireNonNull(luckPerms.getGroupManager().getGroup(groupName));
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
            user.data().clear(NodeType.INHERITANCE::matches);
            Node node = InheritanceNode.builder(group).build();
            user.data().add(node);
        });
    }

    /**
     * {@return whether the provided group exists}
     * @param group the group name to check
     */
    public boolean groupExists(String group) {
        for(Group g : getGroups()) {
            if(g.getName().equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

}
