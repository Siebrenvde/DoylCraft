package dev.siebrenvde.doylcraft.shops;

import dev.siebrenvde.doylcraft.Main;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Shop implements ConfigurationSerializable {

    private OfflinePlayer owner;
    private ItemStack price;
    private Sign sign;
    private Chest mainChest;
    private Chest secondaryChest;

    public static final NamespacedKey NAMESPACED_KEY = new NamespacedKey(Main.getInstance(), "shop_data");

    public Shop(OfflinePlayer owner, ItemStack price, Sign sign, Chest mainChest, Chest secondaryChest) {
        this.owner = owner;
        this.price = price;
        this.sign = sign;
        this.mainChest = mainChest;
        this.secondaryChest = secondaryChest;
    }

    public void update() {
        byte[] serialisedBytes = this.toByteArray();
        if(sign != null) {
            sign.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.BYTE_ARRAY, serialisedBytes);
            sign.update();
        }
        if(mainChest != null) {
            mainChest.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.BYTE_ARRAY, serialisedBytes);
            mainChest.update();
        }
        if(secondaryChest != null) {
            secondaryChest.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.BYTE_ARRAY, serialisedBytes);
            secondaryChest.update();
        }
    }

    public OfflinePlayer getOwner() { return owner; }
    public ItemStack getPrice() { return price; }
    public Sign getSign() { return sign; }
    public Chest getMainChest() { return mainChest; }
    public Chest getSecondaryChest() { return secondaryChest; }

    public void setOwner(OfflinePlayer newOwner) { owner = newOwner; }
    public void setPrice(ItemStack newPrice) { price = newPrice; }
    public void setSecondaryChest(Chest newChest) { secondaryChest = newChest; }

    public boolean isOwner(Player player) { return owner.equals(player); }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("owner", owner.getUniqueId());
        if(price != null) {
            data.put("price", price);
        }
        data.put("sign", sign.getLocation());
        data.put("main_chest", mainChest.getLocation());
        if(secondaryChest != null) {
            data.put("secondary_chest", secondaryChest.getLocation());
        }
        return data;
    }

    public static Shop deserialize(Map<String, Object> data) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID) data.get("owner"));
        ItemStack price = null;
        if(data.containsKey("price")) {
            price = new ItemStack((ItemStack) data.get("price"));
        }
        Sign sign = (Sign) ((Location) data.get("sign")).getBlock().getState();
        Chest mainChest = (Chest) ((Location) data.get("main_chest")).getBlock().getState();
        Chest secondaryChest = null;
        if(data.containsKey("secondary_chest")) {
            secondaryChest = (Chest) ((Location) data.get("secondary_chest")).getBlock().getState();
        }
        return new Shop(owner, price, sign, mainChest, secondaryChest);
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(this);
            outputStream.close();
            return outputStream.toByteArray();
        } catch(Exception e) {
            // TODO: Handle exception
            return null;
        }
    }

    static Shop fromByteArray(byte[] bytes) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Shop shop = (Shop) dataInput.readObject();
            dataInput.close();
            return shop;
        } catch(Exception e) {
            // TODO: Handle exception
            return null;
        }
    }

    public static boolean isShop(TileState tileState) {
        return tileState.getPersistentDataContainer().has(NAMESPACED_KEY);
    }

    public static Shop fromTileState(TileState tileState) {
        if(!isShop(tileState)) { return null; }
        return fromByteArray(tileState.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.BYTE_ARRAY));
    }

    // TODO: Remove, temporary
    public void destroyTesting() {
        if(sign != null) {
            sign.getPersistentDataContainer().remove(NAMESPACED_KEY);
            sign.update();
        }
        if(mainChest != null) {
            mainChest.getPersistentDataContainer().remove(NAMESPACED_KEY);
            mainChest.update();
        }
        if(secondaryChest != null) {
            secondaryChest.getPersistentDataContainer().remove(NAMESPACED_KEY);
            secondaryChest.update();
        }
    }

}
