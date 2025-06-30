package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

/**
 * Listener for {@link org.bukkit.event.player.PlayerInteractEvent}
 * <p>
 * Opens the second door if a player opens a double door
 */
@NullMarked
public class DoubleDoorListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!PlayerData.preferences(event.getPlayer()).openDoubleDoors()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!isNonIronDoor(block.getType())) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getPlayer().isSneaking()) return;

        Door door = (Door) block.getBlockData();
        BlockFace face = switch (door.getFacing()) {
            case NORTH -> BlockFace.EAST;
            case EAST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            default -> throw new IllegalStateException("Unexpected value: " + door.getFacing());
        };

        Door.Hinge hinge = door.getHinge();
        Block otherBlock = block.getRelative(hinge == Door.Hinge.LEFT ? face : face.getOppositeFace());
        if (!isNonIronDoor(otherBlock.getType())) return;

        Door otherDoor = (Door) otherBlock.getBlockData();
        if (otherDoor.getHinge() == hinge) return;

        if (door.isOpen() == otherDoor.isOpen()) {
            otherDoor.setOpen(!otherDoor.isOpen());
            otherBlock.setBlockData(otherDoor);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isNonIronDoor(Material material) {
        return Tag.DOORS.isTagged(material) && material != Material.IRON_DOOR;
    }

}
