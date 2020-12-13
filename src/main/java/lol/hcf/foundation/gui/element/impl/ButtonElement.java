package lol.hcf.foundation.gui.element.impl;

import lol.hcf.foundation.gui.InventoryGui;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * This class can associate an element with a callback when clicked.
 *
 * @param <T> The {@link SingletonElement} to be rendered in the current position.
 */
public class ButtonElement<T extends SingletonElement> extends SingletonElement {

    private final T element;
    private final Consumer<Player> callback;

    public ButtonElement(T element, Consumer<Player> callback) {
        this.element = element;
        this.callback = callback;
    }

    @Override
    public void write(InventoryGui gui, ItemStack[] buffer, MutableInt index) {
        gui.registerElementCallbacks(this, index.intValue());
        this.element.write(gui, buffer, index);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        this.callback.accept((Player) event.getWhoClicked());
        super.onClick(event);
    }
}
