package dev.siebrenvde.doylcraft.addons;

import dev.siebrenvde.doylcraft.DoylCraft;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LuckPermsAddon {

    private static LuckPermsAddon instance;
    private final LuckPerms luckPerms;

    public LuckPermsAddon(DoylCraft doylCraft) {
        instance = this;
        luckPerms = doylCraft.getServer().getServicesManager().load(LuckPerms.class);
    }

    public static LuckPermsAddon get() {
        return instance;
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
    public CompletableFuture<String> getPlayerGroup(OfflinePlayer player) {
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
        Group group = luckPerms.getGroupManager().getGroup(groupName);
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
