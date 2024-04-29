package dev.siebrenvde.doylcraft.shops;

import dev.siebrenvde.doylcraft.Main;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
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
        jsonObject.put("item", item.serialize());
        jsonObject.put("price", price.serialize());
        jsonObject.put("sign", sign.getLocation().serialize());
        jsonObject.put("main_chest", mainChest.getLocation().serialize());
        jsonObject.put("secondary_chest", secondaryChest != null ? secondaryChest.getLocation().serialize() : null);
        return jsonObject.toString();
    }

    public static Shop deserialise(String serialisedString) {
        JSONObject jsonObject = new JSONObject(serialisedString);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(jsonObject.getString("owner"));
        ItemStack item = ItemStack.deserialize(jsonObject.getJSONObject("item").toMap());
        ItemStack price = ItemStack.deserialize(jsonObject.getJSONObject("price").toMap());
        Sign sign = (Sign) Location.deserialize(jsonObject.getJSONObject("sign").toMap()).getBlock().getState();
        Chest mainChest = (Chest) Location.deserialize(jsonObject.getJSONObject("main_chest").toMap()).getBlock().getState();
        Chest secondaryChest = null;
        return new Shop(owner, item, price, sign, mainChest, secondaryChest);
    }

}
