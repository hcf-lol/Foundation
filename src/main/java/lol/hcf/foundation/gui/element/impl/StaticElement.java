package lol.hcf.foundation.gui.element.impl;

import lol.hcf.foundation.gui.InventoryGui;
import lol.hcf.foundation.gui.ItemInfo;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.inventory.ItemStack;

/**
 * A simple static item that will be drawn.
 */
public class StaticElement extends SingletonElement {

    protected ItemStack item;

    public StaticElement(ItemInfo info) {
        this.item = info.getItem();
    }

    @Override
    public void write(InventoryGui gui, ItemStack[] buffer, MutableInt index) {
        buffer[index.intValue()] = this.item;
        index.increment();
        super.write(gui, buffer, index);
    }
}
