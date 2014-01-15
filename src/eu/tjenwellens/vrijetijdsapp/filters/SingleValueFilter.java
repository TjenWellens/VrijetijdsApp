package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcel;

/**
 *
 * @author Tjen
 */
public class SingleValueFilter extends AbstactFilter {
    private String value;

    public SingleValueFilter(String name, String value) {
        super(name);
        this.value = value;
    }

    public boolean match(String value) {
        return this.value.equalsIgnoreCase(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(value);
    }
    public static final Creator<SingleValueFilter> CREATOR = new Creator<SingleValueFilter>() {
        public SingleValueFilter createFromParcel(Parcel in) {
            return new SingleValueFilter(in);
        }

        public SingleValueFilter[] newArray(int size) {
            return new SingleValueFilter[size];
        }
    };

    private SingleValueFilter(Parcel in) {
        super(in);
        this.value = in.readString();
    }
}
