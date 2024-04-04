package dev.siebrenvde.doylcraft.shops;

import dev.siebrenvde.doylcraft.Main;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONArray;
import org.json.JSONObject;

public class Shop {

    private OfflinePlayer owner;
    private ItemStack item;
    private ItemStack price;
    private Sign sign;
    private Chest mainChest;
    private Chest secondaryChest;

    public Shop(OfflinePlayer owner, ItemStack item, ItemStack price, Sign sign, Chest mainChest, Chest secondaryChest) {
        this.owner = owner;
        this.item = item;
        this.price = price;
        this.sign = sign;
        this.mainChest = mainChest;
        this.secondaryChest = secondaryChest;
    }

    public void update() {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "shop_data");
        String serialised = serialise();
        if (sign != null) {
            sign.getPersistentDataContainer().set(key, PersistentDataType.STRING, serialised);
            sign.update();
        }
    }

    public OfflinePlayer getOwner() { return owner; }
    public ItemStack getItem() { return item; }
    public ItemStack getPrice() { return price; }
    public Sign getSign() { return sign; }
    public Chest getMainChest() { return mainChest; }
    public Chest getSecondaryChest() { return secondaryChest; }

    public void setOwner(OfflinePlayer newOwner) { owner = newOwner; }
    public void setItem(ItemStack newItem) { item = newItem; }
    public void setPrice(ItemStack newPrice) { price = newPrice; }
    public void setSecondaryChest(Chest newChest) { secondaryChest = newChest; }

    public String serialise() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("owner", owner.getUniqueId());
        jsonObject.put("item", itemStackToArray(item));
        jsonObject.put("price", itemStackToArray(price));
        jsonObject.put("sign", locationToArray(sign.getLocation()));
        jsonObject.put("main_chest", locationToArray(mainChest.getLocation()));
        jsonObject.put("secondary_chest", secondaryChest != null ? locationToArray(secondaryChest.getLocation()) : null);
        return jsonObject.toString();
    }

    public static Shop deserialise(String serialisedString) {
        JSONObject jsonObject = new JSONObject(serialisedString);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(jsonObject.getString("owner"));
        ItemStack item = arrayToItemStack(jsonObject.getJSONArray("item"));
        ItemStack price = arrayToItemStack(jsonObject.getJSONArray("price"));
        Sign sign = (Sign) arrayToLocation(jsonObject.getJSONArray("sign")).getBlock().getState();
        Chest mainChest = (Chest) arrayToLocation(jsonObject.getJSONArray("main_chest")).getBlock().getState();
        Chest secondaryChest = null;
        return new Shop(owner, item, price, sign, mainChest, secondaryChest);
    }

    private JSONArray itemStackToArray(ItemStack itemStack) {
        return new JSONArray(new Object[]{itemStack.getType().name(), itemStack.getAmount()});
    }

    private static ItemStack arrayToItemStack(JSONArray array) {
        return new ItemStack(Material.getMaterial(array.getString(0)), array.getInt(1));
    }

    private JSONArray locationToArray(Location location) {
        return new JSONArray(new Object[]{location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()});
    }

    private static Location arrayToLocation(JSONArray array) {
        return new Location(Bukkit.getWorld(array.getString(0)), array.getDouble(1), array.getDouble(2), array.getDouble(3));
    }

}
