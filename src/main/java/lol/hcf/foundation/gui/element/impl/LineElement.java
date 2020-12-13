package lol.hcf.foundation.gui.element.impl;

import lol.hcf.foundation.gui.InventoryGui;
import lol.hcf.foundation.gui.ItemInfo;
import lol.hcf.foundation.gui.element.AbstractElement;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.inventory.ItemStack;

/**
 * This element acts as nine blank element slots, to take up one whole row in the
 * inventory gui.
 */
public class LineElement extends AbstractElement {

    private final ItemStack item;

    public LineElement(ItemInfo info) {
        this.item = info.getItem();
    }

    public LineElement() {
        this.item = null;
    }

    @Override
    public void write(InventoryGui gui, ItemStack[] buffer, MutableInt index) {
        for (int i = 0; i < 9; i++) {
            buffer[index.intValue()] = this.item;
            index.increment();
        }

        //super.write(buffer, index);
    }

    @Override
    public int getSize() {
        return 9;
    }
}
