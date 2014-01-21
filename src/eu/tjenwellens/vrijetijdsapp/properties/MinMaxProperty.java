package eu.tjenwellens.vrijetijdsapp.properties;

import android.graphics.Point;

/**
 *
 * @author Tjen
 */
public class MinMaxProperty extends GeneralProperty {
    private static final String DEFAULT_SPLITTER = "-";
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
        return min + DEFAULT_SPLITTER + max;
    }

    public static Point getMinMax(String value) throws NumberFormatException {
        String[] min_max = value.split(DEFAULT_SPLITTER);
        int min = Integer.parseInt(min_max[0]);
        int max = Integer.parseInt(min_max[1]);
        return new Point(min, max);
    }
}
