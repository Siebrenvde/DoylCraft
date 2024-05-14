package dev.siebrenvde.doylcraft.shops;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An item to be sold in a shop
 */
public class ShopItem implements ConfigurationSerializable {

    private ItemStack itemStack;
    private ItemStack price;
    private int fullStackCount;
    private int remaining;

    private ShopItem(ItemStack itemStack, ItemStack price, int fullStackCount, int remaining) {
        this.itemStack = itemStack;
        this.price = price;
        this.fullStackCount = fullStackCount;
        this.remaining = remaining;
    }

    /**
     * Get a ShopItem from an ItemStack
     * @param itemStack the ItemStack
     * @return the ShopItem
     */
    public static ShopItem fromItemStack(ItemStack itemStack) {
        return new ShopItem(itemStack.clone(), null, itemStack.getAmount() == itemStack.getMaxStackSize() ? 1 : 0, 0);
    }

    /**
     * Get the ItemStack
     * @return the ItemStack
     */
    public ItemStack getItemStack() { return itemStack; }

    /**
     * Get the price
     * @return the price of this ShopItem
     */
    @Nullable
    public ItemStack getPrice() { return price; }

    /**
     * Get the amount of full stacks for this ShopItem
     * @return the amount of full stacks
     */
    public int getFullStackCount() { return fullStackCount; }

    /**
     * Get the number of items that don't fit in the full stacks
     * @return the remaining item count
     */
    public int getRemaining() { return remaining; }

    /**
     * Get the total item count
     * @return the total item count
     */
    public int getTotalItemAmount() {
        return (itemStack.getAmount() * (fullStackCount > 0 ? fullStackCount : 1) + remaining);
    }

    /**
     * Compares this ShopItem to another ShopItem
     * @param otherItem the other ShopItem
     * @return true if ShopItems are equal, ignoring item count
     */
    public boolean isSimilar(ShopItem otherItem) {
        return itemStack.isSimilar(otherItem.getItemStack());
    }

    /**
     * Appends a ShopItem to this ShopItem
     * <p>
     * ShopItems must be similar
     * @param otherItem the other ShopItem
     * @return the combined ShopItem, null if not similar
     */
    public ShopItem appendItem(ShopItem otherItem) {
        if(!isSimilar(otherItem)) { return null; }

        int maxStackSize = itemStack.getMaxStackSize();
        int thisSize = itemStack.getAmount();
        int otherSize = otherItem.getItemStack().getAmount();

        if(fullStackCount == 0) {
            int untilFull = maxStackSize - thisSize;

            if(otherSize < untilFull) {
                itemStack.add(otherSize);
                return this;
            }

            itemStack.setAmount(maxStackSize);
            fullStackCount = 1;
            remaining = otherSize - untilFull;
            return this;
        }

        if(otherSize == maxStackSize) {
            fullStackCount++;
            return this;
        }

        if((remaining + otherSize) >= maxStackSize) {
            fullStackCount++;
            otherSize -= (maxStackSize - remaining);
            remaining = 0;
        }

        remaining += otherSize;

        return this;
    }

    /**
     * Serialises this ShopItem to a Map
     * @return the serialised Map
     */
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("item_stack", itemStack);
        data.put("price", price);
        data.put("full_stack_count", fullStackCount);
        data.put("remaining", remaining);

        return data;
    }

    /**
     * Deserialises a Map to a ShopItem
     * @return the deserialised ShopItem
     */
    public static ShopItem deserialize(Map<String, Object> data) {
        return new ShopItem(
            (ItemStack) data.get("item_stack"),
            (ItemStack) data.get("price"),
            (int) data.get("full_stack_count"),
            (int) data.get("remaining")
        );
    }

}
