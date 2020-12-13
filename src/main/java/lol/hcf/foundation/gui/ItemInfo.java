package lol.hcf.foundation.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Simple item descriptor
 */
public class ItemInfo {

    private final ItemStack item;

    public ItemInfo(Material type, int amount) {
        this.item = new ItemStack(type, amount);
        if (type == Material.DIAMOND_BLOCK) System.out.println(System.identityHashCode(this.item));
    }

    public ItemInfo(Material type) {
        this(type, 1);
    }

    public ItemInfo setType(Material type) {
        this.item.setType(type);
        return this;
    }

    public ItemInfo setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemInfo setName(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(name);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemInfo setLore(List<String> lore) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemStack getItem() {
        return item;
    }
}
