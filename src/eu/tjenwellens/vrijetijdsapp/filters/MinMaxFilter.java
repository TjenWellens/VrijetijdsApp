package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcel;

/**
 *
 * @author Tjen
 */
public class MinMaxFilter extends AbstactFilter {
    private int min;
    private int max;

    public MinMaxFilter(String name, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
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
    public String toString() {
        return "" + min + "-" + max;
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

    private MinMaxFilter(Parcel in) {
        super(in);
        this.min = in.readInt();
        this.max = in.readInt();
    }
}
