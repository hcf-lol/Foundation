package lol.hcf.foundation.gui.position;

/**
 * Margin container specifying padding/margins for the gui/flex-elements on the
 * x axis.
 */
public class Margin {

    private int left, right;

    public Margin(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public Margin() {
        this(0, 0);
    }

    public Margin left(int left) {
        this.left = left;
        return this;
    }

    public Margin right(int right) {
        this.right = right;
        return this;
    }

    public Margin set(int left, int right) {
        return this.left(left).right(right);
    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }
}
