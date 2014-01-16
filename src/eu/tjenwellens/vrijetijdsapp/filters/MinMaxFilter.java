package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcel;

/**
 *
 * @author Tjen
 */
public class MinMaxFilter extends AbstactFilter {
    private static final String SPLITTER = "-";
    private int min;
    private int max;

    public MinMaxFilter(String name, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public static MinMaxFilter fromValue(String name, String value) {
        String[] values = value.split(SPLITTER);
        int min = Integer.parseInt(values[0]);
        int max = Integer.parseInt(values[1]);
        return new MinMaxFilter(name, min, max);
    }

    private MinMaxFilter(Parcel in) {
        super(in);
        this.min = in.readInt();
        this.max = in.readInt();
    }

    @Override
    public boolean match(String value) {
        int val;
        // is a number?
        try {
            val = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        // is between min and max
        if (val < min || val > max) {
            return false;
        }

        return true;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(min);
        out.writeInt(max);
    }
    public static final Creator<MinMaxFilter> CREATOR = new Creator<MinMaxFilter>() {
        public MinMaxFilter createFromParcel(Parcel in) {
            return new MinMaxFilter(in);
        }

        public MinMaxFilter[] newArray(int size) {
            return new MinMaxFilter[size];
        }
    };

    public String getValue() {
        return min + SPLITTER + max;
    }

    public FilterType getType() {
        return FilterType.MIN_MAX;
    }
}
