package lol.hcf.foundation.gui;

import lol.hcf.foundation.extension.PlayerData;
import lol.hcf.foundation.gui.element.AbstractElement;
import lol.hcf.foundation.gui.position.Margin;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing an inventory gui, storing elements, position callbacks, etc.
 */
public class InventoryGui {

    private final Inventory inventory;
    private final List<AbstractElement> elements;

    private final ItemStack[] buffer;
    private AbstractElement[] elementStorageCache;

    private final Margin elementMargin;

    public InventoryGui(Player player, int rows) {
        this.inventory = Bukkit.createInventory(player, rows * 9);
        this.buffer = new ItemStack[rows * 9];
        this.elementStorageCache = new AbstractElement[this.buffer.length];

        this.elements = new ArrayList<>();
        this.elementMargin = new Margin();
    }

    public void addElement(AbstractElement element) {
        this.elements.add(element);
    }

    /**
     * Rewrites the current ItemStack buffer in the inventory, calling
     * {@link AbstractElement#write(InventoryGui, ItemStack[], MutableInt)} on every element
     */
    public void write() {
        Arrays.fill(this.elementStorageCache, null);
        MutableInt index = new MutableInt();
        for (AbstractElement element : this.elements) {
            index.add(this.elementMargin.getLeft());
            element.write(this, this.buffer, index);
            index.add(this.elementMargin.getRight());
        }

        this.inventory.setContents(this.buffer);
    }

    public void display() {
        this.write();
        PlayerData.valueOf((Player) this.inventory.getHolder()).setBoundGui(this);
        ((Player) this.inventory.getHolder()).openInventory(this.inventory);
    }

    /**
     * Fired on a click event, if a callback is cached in the position mapping array,
     * that elements callback is processed.
     *
     * @param event The event
     */
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (this.elementStorageCache == null || event.getRawSlot() >= this.elementStorageCache.length) return;
        AbstractElement element = this.elementStorageCache[event.getRawSlot()];
        if (element == null) return;

        event.setCancelled(false);
        element.onClick(event);
    }

    public boolean equals(Inventory other) {
        return this.inventory.equals(other);
    }

    /**
     * Register's an element's callback to be fired when clicked
     */
    public void registerElementCallbacks(AbstractElement element, int slot) {
        if (this.elementStorageCache == null) this.elementStorageCache = new AbstractElement[this.inventory.getSize()];
        this.elementStorageCache[slot] = element;
    }

    public Margin getMargin() {
        return elementMargin;
    }
}
