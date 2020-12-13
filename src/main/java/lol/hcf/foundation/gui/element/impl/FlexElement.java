package lol.hcf.foundation.gui.element.impl;

import lol.hcf.foundation.gui.InventoryGui;
import lol.hcf.foundation.gui.element.AbstractElement;
import lol.hcf.foundation.gui.position.Margin;
import lol.hcf.foundation.gui.position.OverflowMode;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This element functions as a grouping of elements. You can use this to specify lists of buttons
 * with padding / margins in between, and create cycling animations as well.
 */
public class FlexElement extends AbstractElement {

    private final int width;
    private OverflowMode overflowMode;
    private List<AbstractElement> elements;
    private int origin;
    private boolean wraparound;
    private final Margin elementMargin;

    public FlexElement(int width) {
        this.width = width;
        this.elements = new ArrayList<>();
        this.overflowMode = OverflowMode.HIDE;
        this.origin = 0;
        this.wraparound = true;
        this.elementMargin = new Margin();
    }

    public void addElement(AbstractElement element) {
        this.elements.add(element);
    }

    @Override
    public void write(InventoryGui gui, ItemStack[] buffer, MutableInt index) {
        int start = index.intValue();

        int elementIndex = this.origin;
        for (int i = index.intValue(); i - start < this.width; i = index.intValue()) {
            buffer[i] = null;

            if (elementIndex == -1) {
                index.increment();
                continue;
            }

            if (elementIndex >= this.elements.size()) {
                if (this.wraparound) {
                    elementIndex = 0;
                } else {
                    index.increment();
                    continue;
                }
            }

            AbstractElement element = this.elements.get(elementIndex);
            if (i + element.getSize() + this.elementMargin.getLeft() + this.elementMargin.getRight() - start > this.width) {
                index.increment();
                elementIndex = -1;
                continue;
            }

            index.add(this.elementMargin.getLeft());
            element.write(gui, buffer, index);
            index.add(this.elementMargin.getRight());
            elementIndex++;
        }

        super.write(gui, buffer, index);
    }

    @Override
    public int getSize() {
        return this.width;
    }

    public void setOrigin(int start) {
        this.origin = start;
    }

    public int getLastElementIndex() {
        return this.elements.size() - 1;
    }

    public Margin getMargin() {
        return this.elementMargin;
    }

    public FlexElement setWraparound(boolean wraparound) {
        this.wraparound = wraparound;
        return this;
    }

    public FlexElement setOverflowMode(OverflowMode overflowMode) {
        this.overflowMode = overflowMode;
        return this;
    }
}
