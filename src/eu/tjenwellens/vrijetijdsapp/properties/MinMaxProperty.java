package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
public class MinMaxProperty extends GeneralProperty {
    private int min;
    private int max;

    MinMaxProperty(PropertyType type, int min, int max) {
        super(type);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public String toString() {
        return min + "-" + max;
    }
}
