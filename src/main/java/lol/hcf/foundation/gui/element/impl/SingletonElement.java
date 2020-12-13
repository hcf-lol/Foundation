package lol.hcf.foundation.gui.element.impl;

import lol.hcf.foundation.gui.element.AbstractElement;

/**
 * This class is used to restrict a parameter to a singleton element,
 * meaning that the element will only take up one display slot.
 */
public abstract class SingletonElement extends AbstractElement {

    @Override
    public final int getSize() {
        return 1;
    }

}
