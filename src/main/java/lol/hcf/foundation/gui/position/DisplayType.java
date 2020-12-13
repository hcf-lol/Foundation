package lol.hcf.foundation.gui.position;

/**
 * Display types for GUI elements
 */
public enum DisplayType {
    /**
     * Block will pad the current row or remaining elements until the end of the row
     */
    BLOCK {
        public int padRow(int row, int index) {
            return index + row - (index % row);
        }
    },
    /**
     * Inline will only increase the current index by 1, allowing elements to be rendered
     * adjacent to each other.
     */
    INLINE {
        @Override
        public int padRow(int row, int index) {
            return index + 1;
        }
    };

    public abstract int padRow(int row, int index);
}
