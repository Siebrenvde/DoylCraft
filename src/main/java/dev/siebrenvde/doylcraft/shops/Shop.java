package dev.siebrenvde.doylcraft.shops;

import dev.siebrenvde.doylcraft.Main;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;

import java.util.Map;

public class Shop {

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
        String serialised = serialise();
        if(sign != null) {
            sign.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, serialised);
            sign.update();
        }
        if(mainChest != null) {
            mainChest.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, serialised);
            mainChest.update();
        }
        if(secondaryChest != null) {
            secondaryChest.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, serialised);
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

    public String serialise() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("owner", owner.getUniqueId());
        jsonObject.put("price", price != null ? price.serialize() : null);
        jsonObject.put("sign", sign.getLocation().serialize());
        jsonObject.put("main_chest", mainChest.getLocation().serialize());
        jsonObject.put("secondary_chest", secondaryChest != null ? secondaryChest.getLocation().serialize() : null);
        return jsonObject.toString();
    }

    public static Shop deserialise(String serialisedString) {
        JSONObject jsonObject = new JSONObject(serialisedString);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(jsonObject.getString("owner"));
        Map<String, Object> priceMap = jsonObject.getJSONObject("price").toMap();
        ItemStack price = !priceMap.isEmpty() ? ItemStack.deserialize(priceMap) : null;
        Sign sign = (Sign) Location.deserialize(jsonObject.getJSONObject("sign").toMap()).getBlock().getState();
        Chest mainChest = (Chest) Location.deserialize(jsonObject.getJSONObject("main_chest").toMap()).getBlock().getState();
        Map<String, Object> secondaryChestMap = jsonObject.getJSONObject("secondary_chest").toMap();
        Chest secondaryChest = !secondaryChestMap.isEmpty() ? (Chest) Location.deserialize(secondaryChestMap).getBlock().getState() : null;
        return new Shop(owner, price, sign, mainChest, secondaryChest);
    }

    public static boolean isShop(TileState tileState) {
        return tileState.getPersistentDataContainer().has(NAMESPACED_KEY);
    }

}
