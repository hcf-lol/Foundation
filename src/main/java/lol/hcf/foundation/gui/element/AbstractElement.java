package lol.hcf.foundation.gui.element;

import lol.hcf.foundation.gui.InventoryGui;
import lol.hcf.foundation.gui.position.DisplayType;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractElement {

    protected DisplayType type = DisplayType.BLOCK;
    protected boolean clickable = false;

    public void write(InventoryGui gui, ItemStack[] buffer, MutableInt index) {
        index.setValue(this.type.padRow(9, index.intValue()));
        Arrays.stream(this.getClass().getMethods()).filter((m) -> m.isAnnotationPresent(Annotation.class)).collect(Collectors.toList());
    }

    public void onClick(InventoryClickEvent event) {
        if (!this.clickable) event.setCancelled(true);
    }

    public abstract int getSize();

    public DisplayType getType() {
        return this.type;
    }

    public AbstractElement setType(DisplayType type) {
        this.type = type;
        return this;
    }
}
