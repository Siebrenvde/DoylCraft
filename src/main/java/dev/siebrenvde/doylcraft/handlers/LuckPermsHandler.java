package dev.siebrenvde.doylcraft.handlers;

import dev.siebrenvde.doylcraft.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class LuckPermsHandler {

    private LuckPerms luckPerms;

    public LuckPermsHandler(Main main) {
        luckPerms = main.getServer().getServicesManager().load(LuckPerms.class);
    }

    public Set<Group> getGroups() {
        return luckPerms.getGroupManager().getLoadedGroups();
    }

    public String getPlayerGroup(Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        Set<String> groups = user.getNodes(NodeType.INHERITANCE).stream()
                .map(InheritanceNode::getGroupName)
                .collect(Collectors.toSet());
        return groups.iterator().next();
    }

    public void setPlayerGroup(Player player, String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
            user.data().clear(NodeType.INHERITANCE::matches);
            Node node = InheritanceNode.builder(group).build();
            user.data().add(node);
        });

    }

    public boolean groupExists(String group) {
        for(Group g : getGroups()) {
            if(g.getName().toLowerCase().equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

}
